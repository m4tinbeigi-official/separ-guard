package ir.ricksanchez.separ.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import ir.ricksanchez.separ.data.UserModeManager

/**
 * صفحه آغازین برای انتخاب هوشمند سطح کاربری (عادی و ساده در برابر متخصص و پیشرفته)
 */
@Composable
fun ModeSelectionScreen(
    onSelectMode: (UserModeManager.UserMode) -> Unit,
    onPlayAudioGuide: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF1F2B1A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = Color(0xFF00E676),
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "سپر",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "نحوه نمایش نرم‌افزار را انتخاب کنید (همیشه از تنظیمات قابل تغییر است):",
            fontSize = 13.sp,
            color = Color(0xFF8B949E),
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // دکمه راهنمای صوتی
        OutlinedButton(
            onClick = {
                onPlayAudioGuide("به سپر ضد هک خوش آمدید. اگر می‌خواهید برنامه خیلی ساده و با صدا با شما صحبت کند، گزینه کاربر عادی را بزنید. اگر می‌خواهید پنل تخصصی را ببینید، گزینه کاربر حرفه‌ای را لمس کنید.")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676))
        ) {
            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "شنیدن راهنمای صوتی انتخاب حالت", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // گزینه ۱: کاربر عادی (ساده و صدامحور)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectMode(UserModeManager.UserMode.SIMPLE) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = BorderStroke(1.5.dp, Color(0xFF00E676))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF1F2B1A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "من کاربر عادی هستم",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "طراحی فوق‌العاده ساده، دکمه‌های بزرگ و گویا همراه با دستیار صوتی فارسی (مناسب همه)",
                        color = Color(0xFF8B949E),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // گزینه ۲: کاربر حرفه‌ای (پنل امنیت سایبری و ابزارهای عمیق)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectMode(UserModeManager.UserMode.PRO) },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22)),
            border = BorderStroke(1.dp, Color(0xFF30363D))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF21262D)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(30.dp))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "من کاربر حرفه‌ای / متخصص هستم",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "مشاهده لاگ‌های جزئی، تب‌های تخصصی اسکن، بررسی پکیج‌ها و تنظیمات دقیق امنیتی",
                        color = Color(0xFF8B949E),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
