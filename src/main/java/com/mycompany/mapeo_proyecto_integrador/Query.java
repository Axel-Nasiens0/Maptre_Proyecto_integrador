/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.mapeo_proyecto_integrador;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Query {

    // --- AQUÍ ESTÁ EL MÉTODO QUE TE FALTABA PARA TU INICIO DE SESIÓN ---
  public static boolean validarLogin(String email, String pass) {
        Connection con = Conexion.conectar();

        if (con == null) {
            System.out.println("Error crítico: No hay conexión con la base de datos.");
            return false;
        }

        String sql = "SELECT * FROM usuario WHERE correo = ? AND password = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, pass);
            try (ResultSet rs = ps.executeQuery()) {
                boolean exito = rs.next();
                if(exito) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error en Login: " + e.getMessage());
            return false;
        }
    }
    
   public static void registrarUsuario(String name, String email, String pass, String role, String date) {
        Connection con = Conexion.conectar();

        if (con == null) {
            System.out.println("Error crítico: No hay conexión con la base de datos.");
            return;
        }

        String sql = "INSERT INTO usuario (nombre_usuario, correo, password, rol, fecha_registro) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, pass);
            ps.setString(4, role);
            ps.setString(5, date);

            int filasInsertadas = ps.executeUpdate();

            if (filasInsertadas > 0) {
                System.out.println("Inserción exitosa: " + filasInsertadas + " fila insertada");
            } else {
                System.out.println("No se insertó ninguna fila");
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
        }
    }

    // Método para guardar los datos calculados por el usuario
    public static boolean guardarTerreno(String alias, double perimetro, double area) {
        Connection con = Conexion.conectar();
        if (con == null) return false;

        String sql = "INSERT INTO terrenos (alias_terreno, perimetro_m, area_m2) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, alias);
            ps.setDouble(2, perimetro);
            ps.setDouble(3, area);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar terreno: " + e.getMessage());
            return false;
        }
    }

    // Método opcional para consultar los terrenos en la consola (SELECT con ordenamiento)
    public static void consultarTerrenos() {
        Connection con = Conexion.conectar();
        if (con == null) return;

        String sql = "SELECT * FROM terrenos ORDER BY fecha_registro DESC";
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n--- HISTORIAL DE TERRENOS REGISTRADOS ---");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id_terreno") +
                                   " | Alias: " + rs.getString("alias_terreno") +
                                   " | Perímetro: " + rs.getDouble("perimetro_m") + " m" +
                                   " | Área: " + rs.getDouble("area_m2") + " m²" +
                                   " | Fecha: " + rs.getTimestamp("fecha_registro"));
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar terrenos: " + e.getMessage());
        }
    }
   
    
}