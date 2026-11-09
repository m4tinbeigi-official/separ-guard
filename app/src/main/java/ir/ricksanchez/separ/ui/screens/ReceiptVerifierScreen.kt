package ir.ricksanchez.separ.ui.screens

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ir.ricksanchez.separ.data.FakeReceiptAnalyzer
import ir.ricksanchez.separ.utils.CallForwardingAuditor

/**
 * صفحه بررسی تخصصی اسکرین‌شات رسیدهای بانکی برای کشف رسیدسازهای جعلی و پایش دایورت خط
 */
@Composable
fun ReceiptVerifierScreen(
    onPlayAudioGuide: (String) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var bitmapPreview by remember { mutableStateOf<Bitmap?>(null) }

    var destCardNumber by remember { mutableStateOf("") }
    var trackingNumber by remember { mutableStateOf("") }
    var referenceNumber by remember { mutableStateOf("") }
    var verificationResult by remember { mutableStateOf<FakeReceiptAnalyzer.VerificationResult?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        if (uri != null) {
            try {
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }
                bitmapPreview = bitmap

                // اجرای بررسی با تصویر
                verificationResult = FakeReceiptAnalyzer.analyzeReceipt(
                    destinationCard = destCardNumber,
                    referenceNumber = referenceNumber,
                    trackingNumber = trackingNumber,
                    amountTomans = 0,
                    bitmap = bitmap
                )
            } catch (_: Exception) {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // هدر صفحه بررسی رسید
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
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = Color(0xFF00E676),
                    modifier = Modifier.size(52.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "تشخیص رسید جعلی انتقال وجه",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "اسکرین‌شات رسید را انتخاب کنید تا از نظر ساختگی بودن بررسی شود.",
                    fontSize = 12.sp,
                    color = Color(0xFF8B949E),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // دکمه انتخاب عکس اسکرین‌شات رسید
                Button(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF238636)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "انتخاب اسکرین‌شات رسید از گالری", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // پیش‌نمایش تصویر انتخاب‌شده
        if (bitmapPreview != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        bitmap = bitmapPreview!!.asImageBitmap(),
                        contentDescription = "رسید انتخاب‌شده",
                        modifier = Modifier
                            .height(200.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ورودی‌های تکمیلی برای دقت بیشتر
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "اطلاعات نوشته‌شده روی رسید (اختیاری جهت بررسی دقیق‌تر):",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = destCardNumber,
                    onValueChange = { destCardNumber = it },
                    label = { Text("شماره کارت مقصد (مثلا ۶۰۳۷۹۹...)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = referenceNumber,
                    onValueChange = { referenceNumber = it },
                    label = { Text("شماره مرجع / پیگیری (RRN)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        verificationResult = FakeReceiptAnalyzer.analyzeReceipt(
                            destinationCard = destCardNumber,
                            referenceNumber = referenceNumber,
                            trackingNumber = trackingNumber,
                            amountTomans = 0,
                            bitmap = bitmapPreview
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "بررسی اصالت رسید", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        // نتیجه بررسی رسید
        if (verificationResult != null) {
            val res = verificationResult!!
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (res.isSuspicious) Color(0xFF381515) else Color(0xFF2B2011)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (res.isSuspicious) Icons.Default.Warning else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (res.isSuspicious) Color(0xFFFF5252) else Color(0xFFFFC107),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = res.titleFa,
                            color = if (res.isSuspicious) Color(0xFFFF5252) else Color(0xFFFFC107),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    if (res.recognizedBank != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "بانک شناسایی‌شده: ${res.recognizedBank}",
                            color = Color(0xFF00E676),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    res.reasons.forEach { reason ->
                        Row(modifier = Modifier.padding(vertical = 3.dp)) {
                            Text("• ", color = Color(0xFFC9D1D9))
                            Text(reason, color = Color(0xFFC9D1D9), fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onPlayAudioGuide(res.voiceWarning) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF21262D)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "شنیدن هشدار صوتی برای این رسید", color = Color.White, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // بخش پایش و لغو دایورت خط (USSD Forwarding Guard)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161B22))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CallEnd, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "پایشگر دایورت کدهای دستوری (USSD)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "آیا کسی تلفنی از شما خواسته کد *21* یا شبیه آن را بگیرید؟ این ترفند پیامک‌های رمز دوم شما را برای کلاهبردار ارسال می‌کند.",
                    color = Color(0xFF8B949E),
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { CallForwardingAuditor.dialCancelForwarding(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4444)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "لغو فوری تمام دایورت‌های خط (شماره‌گیری #002##)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
