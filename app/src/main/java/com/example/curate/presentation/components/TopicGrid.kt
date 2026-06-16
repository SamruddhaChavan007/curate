package com.example.curate.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.curate.domain.model.TopicsCategory
import com.example.curate.domain.model.safeUserMessage

@Composable
fun TopicGrid(
    topics: LazyPagingItems<TopicsCategory>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    gridState: LazyStaggeredGridState = rememberLazyStaggeredGridState(),
    shouldAnimateItems: Boolean = false,
    animatedItemIds: Set<String> = emptySet(),
    onItemAnimationCompleted: (String) -> Unit = {}
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = gridState,
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp
    ) {
        items(
            count = topics.itemCount,
            key = { index -> topics[index]?.id ?: "topic-placeholder-$index" }
        ) { index ->
            val topic = topics[index]
            if (topic != null) {
                StaggeredGridItem(
                    itemKey = topic.id,
                    index = index,
                    shouldAnimate = shouldAnimateItems && topic.id !in animatedItemIds,
                    onAnimationCompleted = onItemAnimationCompleted
                ) {
                    TopicCard(
                        topic = topic,
                        onClick = {}
                    )
                }
            }
        }

        if (topics.loadState.append is LoadState.Loading) {
            item(key = "topics-append-loading") {
                CurateLoadingContent(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        }

        val appendError = topics.loadState.append as? LoadState.Error
        if (appendError != null) {
            item(key = "topics-append-error") {
                CurateMessageContent(
                    message = appendError.error.safeUserMessage("Unable to load more topics."),
                    actionLabel = "Retry",
                    onAction = topics::retry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}
