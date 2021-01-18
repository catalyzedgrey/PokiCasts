//package com.grey.kotlinpractice.data
//
//import androidx.datastore.core.Serializer
//
//object SettingsSerializer : Serializer<Settings> {
//    override val defaultValue: Settings = Settings.getDefaultInstance()
//
//    override fun readFrom(input: InputStream): Settings {
//        try {
//            return Settings.parseFrom(input)
//        } catch (exception: InvalidProtocolBufferException) {
//            throw CorruptionException("Cannot read proto.", exception)
//        }
//    }
//
//    override fun writeTo(
//        t: Settings,
//        output: OutputStream) = t.writeTo(output)
//}
//
//val settingsDataStore: DataStore<Settings> = context.createDataStore(
//    fileName = "settings.pb",
//    serializer = SettingsSerializer
//)