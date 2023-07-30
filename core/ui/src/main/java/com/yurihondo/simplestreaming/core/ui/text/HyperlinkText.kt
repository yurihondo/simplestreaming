package com.yurihondo.simplestreaming.core.ui.text

import android.content.Intent
import android.net.Uri
import android.text.style.URLSpan
import androidx.annotation.StringRes
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.HtmlCompat

@Composable
fun HyperlinkText(
    @StringRes resourceId: Int,
    modifier: Modifier = Modifier,
) {
    val tag = "URL"
    val htmlText = stringResource(id = resourceId)
    val parsedHtml = HtmlCompat.fromHtml(htmlText, HtmlCompat.FROM_HTML_MODE_COMPACT)
    val annotatedString = buildAnnotatedString {
        append(parsedHtml)
        parsedHtml.getSpans(0, parsedHtml.length, URLSpan::class.java).forEach { span ->
            addStyle(
                style = SpanStyle(color = MaterialTheme.colorScheme.primary, textDecoration = TextDecoration.Underline),
                start = parsedHtml.getSpanStart(span),
                end = parsedHtml.getSpanEnd(span)
            )
            addStringAnnotation(
                tag = tag,
                annotation = span.url,
                start = parsedHtml.getSpanStart(span),
                end = parsedHtml.getSpanEnd(span)
            )
        }
    }

    val context = LocalContext.current
    ClickableText(
        text = annotatedString,
        modifier = modifier,
        style = LocalTextStyle.current.merge(TextStyle(color = MaterialTheme.colorScheme.onSurface)),
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = tag, start = offset, end = offset).firstOrNull()?.let {
                val url = it.item
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    )
}
