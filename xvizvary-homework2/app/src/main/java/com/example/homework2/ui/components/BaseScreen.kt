package com.example.homework2.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.homework2.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseScreen(
    topBarText: String? = null,
    onBackClick: (() -> Unit)? = null,
    placeholderScreenContent: PlaceholderScreenContent? = null,
    showLoading: Boolean = false,
    floatingActionButton: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    topBarContainerColor: Color = MaterialTheme.colorScheme.background,
    scaffoldContainerColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable (paddingValues: PaddingValues) -> Unit,
) {

    Scaffold(
        containerColor = scaffoldContainerColor,
        floatingActionButton = floatingActionButton,
        topBar = {
            TopAppBar(
                title = {
                    if (topBarText != null) {
                        Text(
                            text = topBarText,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(start = 0.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = topBarContainerColor),
                actions = actions,
                navigationIcon = {
                    if (onBackClick != null) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                            )
                        }
                    }
                }
            )
        }
    ) {
        if (placeholderScreenContent != null) {
            PlaceHolderScreen(
                content = placeholderScreenContent
            )
        } else if (showLoading) {
            LoadingScreen()
        } else {
            content(it)

        }
    }

}