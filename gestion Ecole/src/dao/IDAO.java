package dao;
import java.util.List;
import java.sql.SQLException;


public sealed interface IDAO<T> permits EtudiantDAO, EnseignantDAO, AdministrateurDAO {
    void add(T obj) throws SQLException;
    void update(T obj) throws SQLException;
    void delete(int id) throws SQLException;
    T getById(int id) throws SQLException;  // lowercase 'd' in "Id"
    List<T> getAll() throws SQLException;   // lowercase 'L' in "All"
}