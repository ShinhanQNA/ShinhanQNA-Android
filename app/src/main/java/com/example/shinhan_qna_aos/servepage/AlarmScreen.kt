package com.example.shinhan_qna_aos.servepage

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.shinhan_qna_aos.AppRoute
import com.example.shinhan_qna_aos.PushNotification
import com.example.shinhan_qna_aos.R
import com.example.shinhan_qna_aos.TopBar
import com.jihan.lucide_icons.lucide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlarmScreen(navController: NavController, viewModel: AlarmViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val notifications by viewModel.notifications.collectAsState()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(Modifier.systemBarsPadding().fillMaxSize().background(Color.White)) {
        TopBar("알림") { navController.popBackStack() }
        Box(Modifier.fillMaxSize()) {
            if (notifications.isEmpty()) {
                Text("알림이 없습니다.", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(Modifier.fillMaxSize().padding(bottom = 50.dp)) {
                    items(notifications, key = { it.key }) { notification ->
                        AlarmButton(
                            notification = notification,
                            onOpen = {
                                viewModel.markRead(notification)
                                AppRoute.forNotification(notification.type, notification.targetId)?.let { route ->
                                    navController.navigate(route)
                                }
                            },
                            onDelete = { viewModel.delete(notification) }
                        )
                        HorizontalDivider()
                    }
                }
            }
            Text(
                "배너광고",
                modifier = Modifier.fillMaxWidth().height(50.dp).background(Color.Red).align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun AlarmButton(notification: PushNotification, onOpen: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(if (notification.isRead) Color.White else Color(0xffF5F5F5))
            .clickable(onClick = onOpen)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.background(Color(0xffEDEDED), CircleShape).size(48.dp), contentAlignment = Alignment.Center) {
            Icon(painterResource(R.drawable.bell_ring), contentDescription = null)
        }
        Column(Modifier.weight(1f)) {
            Text(notification.title, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (notification.body.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(notification.body, fontSize = 14.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                SimpleDateFormat("yyyy년 MM월 dd일", Locale.KOREAN).format(Date(notification.receivedAt)),
                color = Color(0xffA5A5A5), fontSize = 14.sp
            )
        }
        IconButton(onClick = onDelete) {
            Icon(painterResource(lucide.x), contentDescription = "알림 삭제", tint = Color(0xffDFDFDF))
        }
    }
}
