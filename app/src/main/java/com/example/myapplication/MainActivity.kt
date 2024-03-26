package com.example.myapplication

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    private val addNoteRequestCode = 1
    private val editNoteRequestCode = 2

    private var notes = mutableListOf<Note>()
    private var notesString = ArrayList<String>()
    private var selectedNoteIndex: Int? = null

    private lateinit var noteListView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteListView = findViewById(R.id.noteListView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, notesString)
        noteListView.adapter = adapter

        noteListView.setOnItemClickListener { _, _, position, _ ->
            selectedNoteIndex = position
        }

        val createNoteButton: Button = findViewById(R.id.createNoteButton)
        val openNoteButton: Button = findViewById(R.id.openNoteButton)
        val deleteNoteButton: Button = findViewById(R.id.deleteNoteButton)

        createNoteButton.setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            startActivityForResult(intent, addNoteRequestCode)
        }

        openNoteButton.setOnClickListener {
            selectedNoteIndex?.let { index ->
                val intent = Intent(this, EditNoteActivity::class.java)
                intent.putExtra(EditNoteActivity.EXTRA_NAME, notes[index].name)
                intent.putExtra(EditNoteActivity.EXTRA_BODY, notes[index].body)
                startActivityForResult(intent, editNoteRequestCode)
            }
        }

        deleteNoteButton.setOnClickListener {
            selectedNoteIndex?.let { index ->
                notes.removeAt(index)
                notesString.removeAt(index)
                adapter.notifyDataSetChanged()
                selectedNoteIndex = null
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == addNoteRequestCode) {
                data?.getStringExtra(EditNoteActivity.EXTRA_NAME)?.let { name ->
                    data.getStringExtra(EditNoteActivity.EXTRA_BODY)?.let { body ->
                        val note = Note(name, body)
                        notes.add(note)
                        notesString.add(name)
                        adapter.notifyDataSetChanged()
                    }
                }
            } else if (requestCode == editNoteRequestCode) {
                data?.getStringExtra(EditNoteActivity.EXTRA_NAME)?.let { name ->
                    data.getStringExtra(EditNoteActivity.EXTRA_BODY)?.let { body ->
                        selectedNoteIndex?.let { index ->
                            notes[index] = Note(name, body)
                            notesString[index] = name
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }
    }


    data class Note(val name: String, val body: String)
}

class EditNoteActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var bodyEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        nameEditText = findViewById(R.id.noteNameEditText)
        bodyEditText = findViewById(R.id.noteBodyEditText)

        val name = intent.getStringExtra(EXTRA_NAME)
        val body = intent.getStringExtra(EXTRA_BODY)

        nameEditText.setText(name)
        bodyEditText.setText(body)

        val saveButton: Button = findViewById(R.id.saveButton)
        saveButton.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val name = nameEditText.text.toString().trim()
        val body = bodyEditText.text.toString().trim()

        if (name.isNotEmpty() && body.isNotEmpty()) {
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_NAME, name)
            resultIntent.putExtra(EXTRA_BODY, body)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
        const val EXTRA_BODY = "extra_body"
    }
}