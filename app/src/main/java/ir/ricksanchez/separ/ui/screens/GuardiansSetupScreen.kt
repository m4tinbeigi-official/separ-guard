package ir.ricksanchez.separ.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.InstallMobile
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ricksanchez.separ.ui.components.OemPermissionTutorialDialog
import ir.ricksanchez.separ.utils.OemPermissionHelper

/**
 * صفحه روشن کردن سپرهای سه‌گانه محافظتی ۲۴ ساعته با راهنمایی اختصاصی مدل گوشی
 */
@Composable
fun GuardiansSetupScreen(
    isSubscribed: Boolean,
    onNavigateToPaywall: () -> Unit,
    onPlayAudioGuide: (String) -> Unit
) {
    val context = LocalContext.current
    var smsGuardActive by remember { mutableStateOf(true) }
    var webGuardActive by remember { mutableStateOf(false) }
    var installGuardActive by remember { mutableStateOf(true) }
    var showOemTutorialDialog by remember { mutableStateOf(false) }

    val oemGuide = remember { OemPermissionHelper.getAccessibilityGuide(context) }

    if (showOemTutorialDialog) {
        OemPermissionTutorialDialog(
            guide = oemGuide,
            onDismiss = { showOemTutorialDialog = false },
            onPlayVoiceGuide = { voiceText -> onPlayAudioGuide(voiceText) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // هدر اصلی و نمایش برند گوشی کاربر
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
                    modifier = Modifier.size(52.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "سپرهای محافظتی شبانه‌روزی",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                // نشان برند گوشی کاربر
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF21262D))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "مدل گوشی شما: ${oemGuide.brandNameFa}",
                        color = Color(0xFFC9D1D9),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        onPlayAudioGuide("برای محافظت همیشگی، این سه کلید را روشن نگه دارید تا سپر مراقب پیامک‌ها و درگاه‌های پرداخت باشد.")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676))
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "شنیدن راهنمای صوتی سپرهای دائم", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // اگر اشتراک فعال نباشد، هشدار قفل سپرهای دائمی نمایش داده شود
        if (!isSubscribed) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF261011))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "سپرهای دائمی نیازمند اشتراک سالیانه هستند",
                            color = Color(0xFFFFC107),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "برای فعال‌سازی محافظت ۲۴ ساعته درگاه‌ها و پیامک‌ها، اشتراک را تهیه کنید.",
                            color = Color(0xFFF0F6FC),
                            fontSize = 11.sp
                        )
                    }
                    Button(
                        onClick = onNavigateToPaywall,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "خرید", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // سپر ۱: پیامک
        GuardianToggleCard(
            title = "سپر پیامک‌های کلاهبرداری",
            description = "شناسایی خودکار پیامک‌های جعلی ثنا، سهام عدالت و واریز سود قبل از باز شدن لینک",
            icon = Icons.Default.MarkEmailRead,
            isActive = smsGuardActive,
            onToggle = {
                if (!isSubscribed) onNavigateToPaywall() else smsGuardActive = !smsGuardActive
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // سپر ۲: درگاه پرداخت (با باز کردن دیالوگ راهنمای برند گوشی)
        GuardianToggleCard(
            title = "سپر درگاه‌های جعلی شاپرک",
            description = "مسدودسازی لحظه‌ای صفحات پرداخت تقلبی در مرورگرها برای جلوگیری از دزدیده شدن اطلاعات کارت",
            icon = Icons.Default.CreditCard,
            isActive = webGuardActive,
            actionLabel = "مشاهده راهنمای فعال‌سازی ${oemGuide.brandNameFa}",
            onAction = {
                showOemTutorialDialog = true
            },
            onToggle = {
                if (!isSubscribed) {
                    onNavigateToPaywall()
                } else {
                    showOemTutorialDialog = true
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // سپر ۳: نصب برنامه‌ها
        GuardianToggleCard(
            title = "سپر دانلود و نصب برنامه‌ها",
            description = "بررسی خودکار و درجا هر برنامه‌ای که از تلگرام، ایتا یا اینترنت روی گوشی نصب می‌شود",
            icon = Icons.Default.InstallMobile,
            isActive = installGuardActive,
            onToggle = {
                if (!isSubscribed) onNavigateToPaywall() else installGuardActive = !installGuardActive
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // بخش تنظیمات شروع خودکار در پس‌زمینه (Autostart)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Autorenew, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "روشن ماندن دائم در پس‌زمینه (شروع خودکار)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "در گوشی‌های ${oemGuide.brandNameFa} برای اینکه سپر هنگام بسته شدن برنامه‌ها خاموش نشود، لازم است دسترسی شروع خودکار (Autostart) را فعال کنید.",
                    color = Color(0xFF8B949E),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        OemPermissionHelper.openOemAutostartSettings(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676))
                ) {
                    Text(text = "باز کردن تنظیمات شروع خودکار ${oemGuide.brandNameFa}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun GuardianToggleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isActive) Color(0xFF1F2B1A) else Color(0xFF21262D)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isActive) Color(0xFF00E676) else Color(0xFF8B949E),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.weight(1f)
                )

                Switch(
                    checked = isActive,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF00E676),
                        checkedTrackColor = Color(0xFF1F2B1A)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = description,
                color = Color(0xFF8B949E),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676))
                ) {
                    Text(text = actionLabel, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
