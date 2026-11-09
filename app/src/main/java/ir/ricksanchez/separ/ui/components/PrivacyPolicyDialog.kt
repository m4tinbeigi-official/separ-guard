package ir.ricksanchez.separ.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.VerifiedUser
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
 * بیانیه حریم خصوصی، شفافیت مالی و پشتیبانی نرم‌افزار ضد هک ریک سانچز
 * الزامی برای اخذ تاییدیه انتشار در کافه‌بازار و مایکت
 */
@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

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
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1F2B1A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "سیاست حریم خصوصی و پشتیبانی رسمی",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "توسعه‌دهنده: ریک سانچز (Rick Sanchez)",
                    fontSize = 12.sp,
                    color = Color(0xFF00E676)
                )

                Spacer(modifier = Modifier.height(14.dp))

                PolicySection(
                    title = "۱. تعهد عدم خروج اطلاعات (Zero-Telemetry)",
                    body = "نرم‌افزار 'سپر' تمامی اسکن‌های بدافزار، خواندن الگوهای پیامک و بررسی دامنه‌ها را به صورت ۱۰۰٪ آفلاین روی پردازنده گوشی شما انجام می‌دهد. هیچ شماره تلفن، متن پیامک یا اطلاعات کارتی به هیچ سروری ارسال نمی‌شود."
                )

                Spacer(modifier = Modifier.height(10.dp))

                PolicySection(
                    title = "۲. نحوه استفاده از دسترسی‌پذیری و پیامک",
                    body = "دسترسی پیامک صرفاً برای خنثی‌سازی پیامک‌های کلاهبرداری (ابلاغیه ثنا، سهام عدالت) و دسترسی‌پذیری صرفاً برای پوشاندن درگاه‌های جعلی پرداخت با پوسته قرمز رنگ استفاده می‌شود."
                )

                Spacer(modifier = Modifier.height(10.dp))

                PolicySection(
                    title = "۳. شرایط اشتراک سالیانه و لغو",
                    body = "اشتراک سالیانه به مبلغ ۵۰۰ هزار تومان از بستر رسمی کافه‌بازار یا مایکت فعال می‌شود. بازیابی خرید در صورت حذف و نصب مجدد از طریق دکمه 'بازیابی اشتراک' رایگان و نامحدود است."
                )

                Spacer(modifier = Modifier.height(16.dp))

                // تماس مستقیم با ایمیل پشتیبانی
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:m4tinbeigi@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "پشتیبانی نرم‌افزار سپر ضد هک")
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = Color(0xFF00E676))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "ارسال پیام به ایمیل پشتیبانی توسعه‌دهنده", color = Color.White, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "بستن", color = Color(0xFF8B949E), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun PolicySection(title: String, body: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF21262D))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = body, color = Color(0xFFC9D1D9), fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}
