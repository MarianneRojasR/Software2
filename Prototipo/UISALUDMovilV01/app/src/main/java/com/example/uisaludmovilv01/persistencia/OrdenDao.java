package com.example.uisaludmovilv01.persistencia;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.example.uisaludmovilv01.modelos.Orden;
import com.example.uisaludmovilv01.modelos.Procedimiento;

import java.util.List;

@Dao
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class})
public interface OrdenDao {

    //@Insert

    @Query("SELECT * FROM Ordenes WHERE id = :idUsuario")
    LiveData<List<Orden>> getOrdenesUsuario(int idUsuario);

    @Query("SELECT * FROM Ordenes WHERE id = :idUsuario and tipo = 0")
    LiveData<List<Orden>> consultarOrdenesMedicamento(int idUsuario);

    @Query("SELECT * FROM Ordenes WHERE id = :idUsuario and tipo = 1")
    LiveData<List<Orden>> consultarOrdenesProcedimiento(int idUsuario);


    //@Update


    //@Delete
}