package com.mvvm.hayate.model.main

import com.google.gson.annotations.SerializedName

class RepoResp(
	@SerializedName("items")
	val items: List<Repo> = emptyList()
)