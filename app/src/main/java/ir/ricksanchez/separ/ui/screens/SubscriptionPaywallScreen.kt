package ir.ricksanchez.separ.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
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
import ir.ricksanchez.separ.billing.UnifiedBillingManager

/**
 * صفحه شفاف و ساده خرید اشتراک سالیانه ۵۰۰ هزار تومانی کافه‌بازار و مایکت
 */
@Composable
fun SubscriptionPaywallScreen(
    userPhoneNumber: String?,
    onBuyFromBazaar: () -> Unit,
    onBuyFromMyket: () -> Unit,
    onRestorePurchases: () -> Unit,
    onPlayAudioGuide: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // نشان برجسته امنیت و قیمت
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF1F2B1A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "سپر محافظ دائمی ۲۴ ساعته",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "محافظت همیشگی از حساب بانکی و جلوگیری از هک گوشی",
                    fontSize = 13.sp,
                    color = Color(0xFF8B949E),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // قیمت اشتراک سالیانه به تومان
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0D1117))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "اشتراک سالیانه (۳۶۵ روز)",
                            color = Color(0xFFC9D1D9),
                            fontSize = 13.sp
                        )
                        Text(
                            text = "۵۰۰٬۰۰۰ تومان",
                            color = Color(0xFF00E676),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // راهنمای صوتی
                OutlinedButton(
                    onClick = onPlayAudioGuide,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676))
                ) {
                    Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "شنیدن راهنمای صوتی اشتراک", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // مزایای اشتراک به زبان بسیار ساده
        Text(
            text = "با خرید اشتراک چه امکاناتی فعال می‌شود؟",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        FeatureBenefitItem("محافظت در لحظه از درگاه‌های پرداخت و جلوگیری از خالی شدن حساب")
        FeatureBenefitItem("شنود خودکار پیامک‌های کلاهبرداری (ثنا، سهام، یارانه) قبل از باز شدن")
        FeatureBenefitItem("بررسی درجا و خودکار هر فایلی که از تلگرام، ایتا یا اینترنت دانلود می‌کنید")
        FeatureBenefitItem("ثبت گزارش تمام حملاتی که به شما شده و سپر مانع آنها شده است")

        Spacer(modifier = Modifier.height(24.dp))

        // دکمه‌های خرید در کافه‌بازار و مایکت
        Button(
            onClick = onBuyFromBazaar,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "خرید سالیانه با کافه‌بازار (۵۰۰ هزار تومان)",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onBuyFromMyket,
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF00E676))
        ) {
            Text(
                text = "خرید سالیانه با مایکت",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = onRestorePurchases,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "قبلاً خریده‌اید؟ بازیابی اشتراک قبلی",
                color = Color(0xFF8B949E),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        var showPrivacyPolicy by remember { mutableStateOf(false) }
        if (showPrivacyPolicy) {
            ir.ricksanchez.separ.ui.components.PrivacyPolicyDialog(onDismiss = { showPrivacyPolicy = false })
        }

        TextButton(
            onClick = { showPrivacyPolicy = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "سیاست حریم خصوصی، پشتیبانی و شرایط لغو اشتراک",
                color = Color(0xFF00E676),
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FeatureBenefitItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF00E676),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = Color(0xFFC9D1D9),
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}
