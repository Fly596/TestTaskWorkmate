package com.example.testtaskworkmate.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.testtaskworkmate.data.source.network.NetworkCharacter

class CharactersPagingSource : PagingSource<Int, NetworkCharacter>() {

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, NetworkCharacter> {
        TODO("Not yet implemented")
    }

    override fun getRefreshKey(
        state: PagingState<Int, NetworkCharacter>
    ): Int? {
        TODO("Not yet implemented")
    }
}
