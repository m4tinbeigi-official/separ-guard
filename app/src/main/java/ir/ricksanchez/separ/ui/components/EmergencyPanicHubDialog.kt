package ir.ricksanchez.separ.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CreditCardOff
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

/**
 * جعبه کمک‌های اولیه هک: تماس اضطراری با پلیس فتا و مسدودسازی فوری کارت تمام بانک‌ها بدون نیاز به اینترنت
 */
@Composable
fun EmergencyPanicHubDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val emergencyHotlines = listOf(
        Pair("بانک ملی ایران", "02164140"),
        Pair("بانک ملت", "02182488"),
        Pair("بانک صادرات ایران", "02109602"),
        Pair("بانک تجارت", "0211554"),
        Pair("بانک سپه", "0211557"),
        Pair("بانک پاسارگاد (ویپاد)", "02182890"),
        Pair("بانک سامان (بلوبانک)", "0216422"),
        Pair("بانک پارسیان", "02181151"),
        Pair("بانک قرض‌الحسنه رسالت", "0214747"),
        Pair("بانک آینده", "02127663200")
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(4.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF381515)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Emergency, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "جعبه کمک‌های اولیه هک و نجات حساب",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "اقدامات اورژانسی برای مسدودسازی سریع کارت",
                            color = Color(0xFF8B949E),
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // تماس با مرکز فوریت‌های سایبری پلیس فتا
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:096380")).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF261011))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocalPolice, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(30.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "تماس با پلیس فتا (۰۹۶۳۸۰)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "مرکز فوریت‌های سایبری کشور (شبانه روزی)", color = Color(0xFFFF5252), fontSize = 11.sp)
                        }
                        Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = Color(0xFFFF5252))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "شماره‌گیری فوری تلفن‌بانک برای سوزاندن کارت:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(emergencyHotlines) { (bankName, phone) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    context.startActivity(intent)
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF21262D))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.CreditCardOff, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = bankName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(text = "شماره تلفن‌بانک: $phone", color = Color(0xFF8B949E), fontSize = 11.sp)
                                }
                                Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "بستن جعبه اضطراری", color = Color(0xFF8B949E), fontSize = 12.sp)
                }
            }
        }
    }
}
