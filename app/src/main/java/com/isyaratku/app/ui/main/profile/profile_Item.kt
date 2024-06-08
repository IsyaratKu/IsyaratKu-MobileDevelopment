package com.isyaratku.app.ui.main.profile

data class profile_Item(

    val type: Int,
    val iconResId: Int = 0 ,
    val title : String = ""

)
{
    companion object{
        const val TYPER_HEADER = 0
        const val TYPER_ITEM = 1
        const val TYPER_DARK_MODE = 2

    }
}
