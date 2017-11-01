package dao;

import java.sql.SQLException;
import java.util.List;

import model.Curso;

public interface ICursoDao {

	public List<Curso> listarCursos()throws SQLException;
	
}
