package com.example.senpos.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.senpos.ui.theme.SenPosTheme

@Composable
fun IntakeComponent() {
    Surface(modifier = Modifier.fillMaxWidth()) {
        Column (modifier = Modifier.padding(top = 16.dp, bottom = 16.dp, start = 8.dp, end = 8.dp)){
            Row (
                modifier = Modifier.fillMaxWidth().padding(bottom=8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Paracétamol 500g",
                    fontSize = 24.sp
                )

                Text(
                    text = "1 comprimé",
                    fontSize = 20.sp,
                    fontStyle = FontStyle.Italic
                )
            }

            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { /*TODO*/ }) {
                    Text(
                        text = "Je l'ai pris"
                    )
                }

                Button(onClick = { /*TODO*/ }) {
                    Text(
                        text = "J'ai oublié"
                    )
                }
            }

        }
    }
}

@Composable
@Preview
fun IntakePreview() {
    SenPosTheme {
        IntakeComponent()
    }
}