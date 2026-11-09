package ir.ricksanchez.separ.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.window.Dialog

/**
 * دیالوگ رسمی افشای صریح نحوه استفاده از دسترسی‌ها (Prominent Disclosure)
 * مطابق الزامات اکید کافه‌بازار، مایکت و خط‌مشی حریم خصوصی گوگل
 */
@Composable
fun ProminentDisclosureDialog(
    permissionTitle: String,
    purposeFa: String,
    onAccept: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1F2B1A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "اطلاعیه شفاف حریم خصوصی و مجوز",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = permissionTitle,
                    fontSize = 13.sp,
                    color = Color(0xFF00E676),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF21262D))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "چرا به این مجوز نیاز داریم؟",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = purposeFa,
                            color = Color(0xFFC9D1D9),
                            fontSize = 11.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // تعهدات قطعی امنیتی برنامه
                DisclosureCheckItem("پردازش ۱۰۰٪ آفلاین و محلی روی گوشی شما")
                DisclosureCheckItem("عدم ذخیره، عدم ضبط و عدم ارسال رمزها به هیچ سروری")
                DisclosureCheckItem("استفاده صرفاً برای مسدودسازی صفحات پرداخت جعلی و فیشینگ")

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "موافقم و رفتن به تنظیمات", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "فعلاً نه", color = Color(0xFF8B949E), fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun DisclosureCheckItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00E676), modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = Color(0xFF8B949E), fontSize = 11.sp)
    }
}
