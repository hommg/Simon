package com.umsl.gregoryhommert.simon

import android.os.Environment
import android.util.JsonReader
import android.util.JsonToken
import android.util.JsonWriter
import android.util.Log
import java.io.*

//SOURCE:- The following page was heavily consulted while constructing the
// read and write functionality of this class:
// https://developer.android.com/reference/android/util/JsonWriter.html
class HighScoresModel(fileName: String, packageName: String) {

    //MARK:- Vars
    private var scores: ArrayList<Int>
    private val SIZE = 10                  //NOTE:- Only top 10 high scores persisted
    private var filePath: String

    //MARK:- Init
    init {
        this.filePath = createFilePath(packageName, fileName)
        this.scores = ArrayList<Int>()
    }

    private fun createFilePath(directory: String, file: String): String {
        val externalStoragePath = Environment.getExternalStorageDirectory()
        val directoryPath = externalStoragePath.absolutePath+"/"+directory
        if (File(directoryPath).mkdirs() != true) {
            Log.e("MESSAGE", "$directoryPath already exists!")
        }

        return directoryPath+"/"+file
    }

    //MARK:- Persistence
    fun populate() {
        //MARK:- Clear Before Populating
        clearHighScores()

        //MARK:- Create File
        val file = File(this.filePath)
        if(file.exists()) {
            //MARK:- Streams/Readers
            val fileInputStream = FileInputStream(file)
            val inputStreamWriter = InputStreamReader(fileInputStream, "UTF-8")
            val jsonReader = JsonReader(inputStreamWriter)

            //MARK:- Read
            jsonReader.beginObject()
            val name = jsonReader.nextName()
            if (name.equals("scores") && jsonReader.peek() != JsonToken.NULL) {
                jsonReader.beginArray()
                while (jsonReader.hasNext()) {
                    this.scores.add(jsonReader.nextInt())
                }
                jsonReader.endArray()
                jsonReader.endObject()
            } else {
                Log.e("MESSAGE", "ERROR parsing JSON")
            }

            //MARK:- Close Streams/Readers
            jsonReader.close()
            inputStreamWriter.close()
            fileInputStream.close()
            Log.e("MESSAGE", "${this.scores}")
        } else {
            Log.e("MESSAGE", "$file does not exist!")
        }
    }

    fun persist() {
        //MARK:- Sort Before Persisting
        sortScores()

        //MARK:- Create File
        val file = File(this.filePath)
        if(file.exists()) {
            file.delete()
        }

        file.createNewFile()

        //MARK:- Streams/Writers
        val fileOutputStream = FileOutputStream(file)
        val outputStreamWriter = OutputStreamWriter(fileOutputStream, "UTF-8")
        val jsonWriter = JsonWriter(outputStreamWriter)

        //MARK:- Write
        jsonWriter.setIndent("  ")
        jsonWriter.beginObject()
        jsonWriter.name("scores")
        jsonWriter.beginArray()
        for (score in this.scores) {
            jsonWriter.value(score)
        }
        jsonWriter.endArray()
        jsonWriter.endObject()

        //MARK:- Close Streams/Writers
        jsonWriter.close()
        outputStreamWriter.close()
        fileOutputStream.close()
    }

    //MARK:- Mutations
    fun addIfPossible(value: Int): Boolean {
        when (this.scores.size < SIZE) {
            true -> {
                this.scores.add(value)
                sortScores()
                return true
            }
            false -> {
                val lowerScores = this.scores.filter { it < value }
                when (lowerScores.isNotEmpty()) {
                    true -> {
                        this.scores.remove(lowerScores.min()!!)
                        this.scores.add(value)
                        sortScores()
                        return true
                    }
                    else -> return false
                }
            }
        }
    }

    private fun sortScores() {
        this.scores.sortDescending()
    }

    fun clearHighScores() {
        if (this.scores.isNotEmpty()) {
            this.scores.clear()
        }
    }

    //MARK:- Getters
    fun getHighScores(): ArrayList<Int> {
        return this.scores
    }

    fun getHighestScore(): Int {
        return this.scores.max()!!
    }
}