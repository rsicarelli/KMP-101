interface Platform {
    val name: String
}

expect fun getPlatform(): Platform


expect const val ExpectString: String = ""
