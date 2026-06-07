package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "academic_documents")
data class AcademicDocument(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val contentText: String,
    val manuscriptType: String,
    val reviewMode: String,
    val commentStyle: String,
    val timestamp: Long = System.currentTimeMillis(),
    val overallScore: Int = 0,
    val categoryStatus: String = "Belum Di-review",
    val analysisResultJson: String? = null
)

@Dao
interface AcademicDocumentDao {
    @Query("SELECT * FROM academic_documents ORDER BY timestamp DESC")
    fun getAllDocuments(): Flow<List<AcademicDocument>>

    @Query("SELECT * FROM academic_documents WHERE id = :id LIMIT 1")
    suspend fun getDocumentById(id: Int): AcademicDocument?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: AcademicDocument): Long

    @Query("DELETE FROM academic_documents WHERE id = :id")
    suspend fun deleteDocumentById(id: Int)

    @Query("DELETE FROM academic_documents")
    suspend fun deleteAllDocuments()
}

@Database(entities = [AcademicDocument::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun academicDocumentDao(): AcademicDocumentDao
}

class AcademicDocRepository(private val dao: AcademicDocumentDao) {
    val allDocuments: Flow<List<AcademicDocument>> = dao.getAllDocuments()

    suspend fun getDocumentById(id: Int): AcademicDocument? = dao.getDocumentById(id)

    suspend fun insert(document: AcademicDocument): Long = dao.insertDocument(document)

    suspend fun deleteById(id: Int) = dao.deleteDocumentById(id)
}
