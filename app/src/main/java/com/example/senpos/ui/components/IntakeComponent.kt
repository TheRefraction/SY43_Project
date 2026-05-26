package com.example.senpos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.senpos.ui.theme.SenPosTheme

@Composable
fun IntakeComponent(today: Boolean, upcoming: Boolean) {
    Surface(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color(0xFFFF6C00),
            shape = RoundedCornerShape(50.dp)
        )
    {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Paracetamol 500g",
                    color = Color(0xFFFFFFFF),
                    fontSize = 24.sp
                )

                Text(
                    text = "1 pill",
                    color = Color(0xFFFFFFFF),
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!today) {
                    Button(onClick = { /*TODO*/ }) {
                        Text(
                            text = "I took it"
                        )
                    }

                    Button(onClick = { /*TODO*/ }) {
                        Text(
                            text = "I forgot"
                        )
                    }
                } else {
                    Button(onClick = { /*TODO*/ }, enabled = !upcoming) {
                        Text(
                            text = if (!upcoming) "I took it" else "upcoming"
                        )
                    }
                }

            }

        }
    }
}

@Composable
@Preview
fun IntakePreview() {
    SenPosTheme {
        IntakeComponent(today = true, upcoming = true)
    }
}