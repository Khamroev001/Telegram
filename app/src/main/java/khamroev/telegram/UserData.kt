package khamroev.telegram

import java.io.Serializable

data class UserData (
    var name:String? = null,
    var uid: String?= null,
    var email: String?= null,
    var photo:String?= null
):Serializable {

    constructor() : this(null,null,null,null)
}
