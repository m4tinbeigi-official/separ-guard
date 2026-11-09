package ir.ricksanchez.separ.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ricksanchez.separ.data.RescueLedgerRepository
import ir.ricksanchez.separ.data.models.RescuedAttack
import ir.ricksanchez.separ.data.models.RescuedAttackType

/**
 * صفحه کارنامه شفاف نجات و دفاع‌های انجام شده از گوشی و حساب بانکی کاربر
 * طراحی شده برای درک فوری حتی بدون نیاز به خواندن متن‌های پیچیده
 */
@Composable
fun RescueLedgerScreen(
    stats: RescueLedgerRepository.RescueStats,
    attackList: List<RescuedAttack>,
    onPlayVoiceReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(16.dp)
    ) {
        // ویجت خلاصه بالای صفحه با دکمه پخش صدای گویا
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(56.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "کارنامه نجات و محافظت از شما",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "سپر تا این لحظه جلوی ${stats.totalAttacksBlocked} حمله به گوشی و کارت شما را گرفته است.",
                    fontSize = 14.sp,
                    color = Color(0xFF8B949E),
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // دکمه بزرگ پخش صوتی برای افراد کم‌سواد
                Button(
                    onClick = onPlayVoiceReport,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF238636)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "شنیدن گزارش صوتی نجات‌ها",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "حملاتی که با موفقیت خنثی شدند:",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (attackList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "سپر فعال است و تا الان خطری متوجه شما نشده است.",
                    color = Color(0xFF8B949E),
                    fontSize = 14.sp
                )
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(attackList) { attack ->
                    RescuedAttackItem(attack)
                }
            }
        }
    }
}

@Composable
fun RescuedAttackItem(attack: RescuedAttack) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // آیکون نوع حمله
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (attack.financialProtection) Color(0xFF381515) else Color(0xFF1F2B1A)
                    ),
                contentAlignment = Alignment.Center
            ) {
                val icon = when (attack.type) {
                    RescuedAttackType.FAKE_SHAPARAK_GATEWAY -> Icons.Default.CreditCard
                    RescuedAttackType.PHISHING_SMS -> Icons.Default.MarkEmailRead
                    else -> Icons.Default.Security
                }
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (attack.financialProtection) Color(0xFFFF5252) else Color(0xFF00E676),
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = attack.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "خنثی شد",
                        color = Color(0xFF00E676),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = attack.summaryFa,
                    color = Color(0xFF8B949E),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "تاریخ: ${attack.jalaliDate}",
                    color = Color(0xFF6E7681),
                    fontSize = 11.sp
                )
            }
        }
    }
}
