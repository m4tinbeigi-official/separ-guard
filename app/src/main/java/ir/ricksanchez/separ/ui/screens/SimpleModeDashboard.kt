package ir.ricksanchez.separ.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ricksanchez.separ.data.ThreatPackageRegistry
import ir.ricksanchez.separ.utils.SecurityScoreCalculator

/**
 * داشبورد تک‌دکمه‌ای فوق‌العاده ساده (One-Tap Master Shield)
 * طراحی شده برای کاربران عادی، سالمندان و افراد کم‌سواد بدون نیاز به دانش فنی
 */
@Composable
fun SimpleModeDashboard(
    isScanning: Boolean,
    detectedThreats: List<ThreatPackageRegistry.ThreatResult>,
    securityScore: SecurityScoreCalculator.ScoreBreakdown,
    onTriggerScan: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    onOpenReceiptVerifier: () -> Unit,
    onOpenSessions: () -> Unit,
    onOpenEmergencyHub: () -> Unit,
    onSwitchToProMode: () -> Unit,
    onPlayVoiceGuide: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_pulse"
    )

    val isClean = detectedThreats.isEmpty() && !isScanning

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // نوار بالا: عنوان برند + دکمه تغییر به حالت پیشرفته
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "سپر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            // دکمه سوییچ به حالت حرفه‌ای
            OutlinedButton(
                onClick = onSwitchToProMode,
                shape = RoundedCornerShape(20.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                border = BorderStroke(1.dp, Color(0xFF30363D))
            ) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = Color(0xFF00E5FF), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "حالت پیشرفته", color = Color(0xFF00E5FF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // نشانگر نمره امنیت دستگاه (Gamification Gauge)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "نمره امنیت گوشی شما:", color = Color(0xFF8B949E), fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = securityScore.statusTitleFa, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (securityScore.totalScore >= 70) Color(0xFF1F2B1A) else Color(0xFF381515)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${securityScore.totalScore}",
                        color = if (securityScore.totalScore >= 70) Color(0xFF00E676) else Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // سپر دایره‌ای بزرگ لمسی در مرکز صفحه (Master Shield)
        Box(
            modifier = Modifier
                .size(190.dp)
                .scale(if (!isClean) pulseScale else 1f)
                .clip(CircleShape)
                .background(
                    if (isClean) Color(0xFF14301B) else Color(0xFF4A1417)
                )
                .clickable {
                    if (!isClean) {
                        onNavigateToPaywall()
                    } else {
                        onTriggerScan()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = if (isClean) Icons.Default.Shield else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (isClean) Color(0xFF00E676) else Color(0xFFFF5252),
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isScanning) {
                        "در حال بررسی..."
                    } else if (isClean) {
                        "گوشی امن است\n(لمس برای اسکن)"
                    } else {
                        "خطر شناسایی شد!\n(لمس برای پاکسازی)"
                    },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // دکمه دستیار صوتی
        OutlinedButton(
            onClick = {
                val voiceText = if (isClean) {
                    "گوشی و حساب شما کاملاً امن است. برای بررسی مجدد دایره سبز را لمس کنید."
                } else {
                    "توجه! روی گوشی شما برنامه‌های خطرناک پیدا شده است. برای پاکسازی گوشی دایره قرمز را لمس کرده و اشتراک را فعال کنید."
                }
                onPlayVoiceGuide(voiceText)
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676))
        ) {
            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "شنیدن راهنمای صوتی وضعیت گوشی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ۳ کاشی اقدام سریع برای کاربران عادی
        Text(text = "اقدامات ضروری و محافظت:", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(10.dp))

        SimpleActionCard(
            title = "بررسی عکس فیش یا رسید بانکی",
            description = "آیا مشتری برای شما رسید واریز فرستاده؟ اسکرین‌شات را انتخاب کنید تا جعلی بودن آن بررسی شود.",
            icon = Icons.Default.ReceiptLong,
            onClick = onOpenReceiptVerifier
        )

        Spacer(modifier = Modifier.height(10.dp))

        SimpleActionCard(
            title = "اخراج هکر از تلگرام، ایتا و همراه بانک‌ها",
            description = "بررسی دستگاه‌های متصل به حساب‌های شما و پایان دادن به نشست‌های افراد ناشناس با ۳ لمس ساده.",
            icon = Icons.Default.Devices,
            onClick = onOpenSessions
        )

        Spacer(modifier = Modifier.height(10.dp))

        SimpleActionCard(
            title = "جعبه کمک‌های اولیه (سوزاندن فوری کارت و تماس با فتا)",
            description = "تماس اضطراری شبانه‌روزی با پلیس فتا (۰۹۶۳۸۰) و تلفن‌بانک‌های رسمی برای مسدودسازی سریع کارت.",
            icon = Icons.Default.Emergency,
            isEmergency = true,
            onClick = onOpenEmergencyHub
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SimpleActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isEmergency: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isEmergency) Color(0xFF261011) else Color(0xFF161B22)),
        border = if (isEmergency) BorderStroke(1.dp, Color(0xFFFF5252)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isEmergency) Color(0xFF381515) else Color(0xFF1F2B1A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isEmergency) Color(0xFFFF5252) else Color(0xFF00E676),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = description,
                    color = Color(0xFF8B949E),
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
