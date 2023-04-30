import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.x256n.sdtrainingimagepreparer.desktop.theme.spaces

@Composable
fun WinTextField(
    text: String,
    title: String? = null,
    modifier: Modifier = Modifier.fillMaxWidth(),
    titleModifier: Modifier = Modifier,
    fieldModifier: Modifier = Modifier.width(80.dp),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        title?.let {
            Text(
                modifier = titleModifier
                    .padding(end = MaterialTheme.spaces.extraSmall),
                text = title
            )
        }
        BasicTextField(
            modifier = fieldModifier
                .border(
                    BorderStroke(
                        width = 1.dp,
                        color = Color.Gray
                    )
                )
                .padding(2.dp),
            value = text,
            singleLine = singleLine,
            maxLines = maxLines,
            onValueChange = onValueChange
        )
    }
}