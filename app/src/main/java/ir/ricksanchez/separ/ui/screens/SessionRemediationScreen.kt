package ir.ricksanchez.separ.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
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
import ir.ricksanchez.separ.data.IranianBankRegistry
import ir.ricksanchez.separ.utils.IntentHelper

/**
 * صفحه راهنمای دیداری ۳ مرحله‌ای اخراج افراد ناشناس از شبکه‌های اجتماعی،
 * پایش نشست‌های اپراتوری سیم‌کارت و همراه بانک‌های نصب‌شده روی گوشی
 */
@Composable
fun SessionRemediationScreen(
    onPlayAudioGuide: (String) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // استخراج خودکار همراه بانک‌های نصب‌شده روی دیوایس
    val installedBanks = remember { IranianBankRegistry.detectInstalledBanks(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // هدر کارت راهنما
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
                    imageVector = Icons.Default.Devices,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(52.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "نجات حساب‌ها و اخراج افراد ناشناس",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "اگر کلاهبردار وارد تلگرام، همراه بانک یا سیم‌کارت شما شده، او را بیرون بیندازید.",
                    fontSize = 13.sp,
                    color = Color(0xFF8B949E),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        val voiceText = if (installedBanks.isNotEmpty()) {
                            "سپر ${installedBanks.size} برنامه بانکی روی گوشی شما پیدا کرد. لطفاً وارد تک‌تک آن‌ها شوید و در بخش امنیت، دستگاه‌های متصل را بررسی و افراد ناشناس را بیرون بیندازید."
                        } else {
                            "برای اخراج نفوذگر از تلگرام، ایتا و حساب‌های بانکی، روی نام برنامه‌ها بزنید، وارد دستگاه‌ها شوید و دکمه قرمز خروج را لمس کنید."
                        }
                        onPlayAudioGuide(voiceText)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676))
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "شنیدن راهنمای صوتی اخراج نفوذگر", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // بخش اختصاصی: همراه بانک‌های کشف‌شده روی گوشی کاربر
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2B1A))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "همراه بانک‌های شناسایی‌شده روی گوشی شما (${installedBanks.size} برنامه)",
                        color = Color(0xFF00E676),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "کلاهبرداران ممکن است با سرقت پیامک تایید، در گوشی دیگری به همراه بانک شما متصل شده باشند. وارد تک‌تک این برنامه‌ها شوید و دستگاه‌های متصل را بررسی کنید:",
                    color = Color(0xFFC9D1D9),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (installedBanks.isEmpty()) {
                    Text(
                        text = "هیچ همراه بانک فعالی روی این دستگاه شناسایی نشد.",
                        color = Color(0xFF8B949E),
                        fontSize = 12.sp
                    )
                } else {
                    installedBanks.forEach { bank ->
                        BankSessionItem(bank = bank) {
                            try {
                                val launchIntent = context.packageManager.getLaunchIntentForPackage(bank.packageName)
                                if (launchIntent != null) {
                                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    context.startActivity(launchIntent)
                                }
                            } catch (_: Exception) {}
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // بخش هشدار حیاتی اپراتورهای سیم‌کارت (ایرانسل من و همراه من)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF261011))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "هشدار بسیار مهم: حساب‌های ایرانسل من و همراه من",
                        color = Color(0xFFFF5252),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "حتی اگر ایرانسل من یا همراه من را روی گوشی خود نصب ندارید، حتماً وارد آن‌ها شوید و بخش 'دستگاه‌های فعال' را چک کنید! کلاهبردار ممکن است با شماره شما در گوشی دیگری وارد این برنامه‌ها شده باشد تا پیامک‌ها و دایورت خط شما را کنترل کند.",
                    color = Color(0xFFF0F6FC),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://my.irancell.ir")).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("ایرانسل من", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://my.mci.ir")).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("همراه من", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // دیاگرام ۳ مرحله‌ای مشترک برای پیام‌رسان‌ها
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF21262D))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "۳ مرحله ساده در پیام‌رسان‌ها:",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StepIconItem("۱", Icons.Default.Settings, "تنظیمات")
                    Text("←", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                    StepIconItem("۲", Icons.Default.Devices, "دستگاه‌ها")
                    Text("←", color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                    StepIconItem("۳", Icons.Default.Delete, "خروج همه", isRed = true)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "اخراج نشست در پیام‌رسان‌ها:",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        MessengerCard("تلگرام (Telegram)", "مسیر: تنظیمات > دستگاه‌ها > خروج از سایر نشست‌ها") {
            IntentHelper.openTelegramSessions(context)
        }

        Spacer(modifier = Modifier.height(10.dp))

        MessengerCard("روبیکا (Rubika)", "مسیر: تنظیمات > دستگاه‌های فعال > پایان دادن به نشست‌ها") {
            IntentHelper.openRubikaSessions(context)
        }

        Spacer(modifier = Modifier.height(10.dp))

        MessengerCard("ایتا (Eitaa)", "مسیر: تنظیمات > دستگاه‌های متصل > خروج از سایر دستگاه‌ها") {
            IntentHelper.openEitaaSessions(context)
        }

        Spacer(modifier = Modifier.height(10.dp))

        MessengerCard("بله (Bale)", "مسیر: تنظیمات > امنیت و حریم خصوصی > دستگاه‌های متصل") {
            IntentHelper.openBaleSessions(context)
        }

        Spacer(modifier = Modifier.height(10.dp))

        MessengerCard("اینستاگرام (Instagram)", "مسیر: تنظیمات > امنیت > فعالیت‌های ورود") {
            IntentHelper.openInstagramSessions(context)
        }

        Spacer(modifier = Modifier.height(10.dp))

        MessengerCard("واتساپ (WhatsApp)", "مسیر: تنظیمات > دستگاه‌های متصل > خروج") {
            IntentHelper.openWhatsAppSessions(context)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun BankSessionItem(bank: IranianBankRegistry.DetectedBankApp, onOpen: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = bank.bankTitle, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = bank.sessionPathHint, color = Color(0xFF00E676), fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onOpen,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF238636)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "بررسی", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StepIconItem(number: String, icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, isRed: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isRed) Color(0xFF381515) else Color(0xFF161B22)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isRed) Color(0xFFFF5252) else Color(0xFF00E676),
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "$number. $title", color = Color(0xFFC9D1D9), fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MessengerCard(name: String, pathHint: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = pathHint, color = Color(0xFF8B949E), fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF238636)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "باز کردن", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
