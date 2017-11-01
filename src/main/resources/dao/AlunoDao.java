package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Aluno;
import model.Equivalencia;

public class AlunoDao implements IAlunoDao {

	@Override
	public boolean adicionar(Aluno a) throws SQLException {
		String sql = "Insert into infoAlunos (ra, nome, email, empresa, id_curso, senha, inicio, termino, termoCompromisso, planoAtividade,"
				+ " relatorio1, relatorio2, relatorio3, relatorio4, termoAditivo, avAluno, avEmpresa, termoRecisao, termoRealizacao, termoContrato, ativo, obrigatorio, cargaHoraria)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection con = null;
		PreparedStatement ps = null;
		boolean add = false;

		try {
			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);

			System.out.println("Curso: " + a.getId_curso());

			ps.setString(1, a.getRa());
			ps.setString(2, a.getNome());
			ps.setString(3, a.getEmail());
			ps.setString(4, a.getEmpresa());
			ps.setInt(5, a.getId_curso());
			ps.setString(6, "123");
			java.sql.Date inicio = new java.sql.Date(a.getInicio().getTime());
			ps.setDate(7, inicio);
			java.sql.Date termino = new java.sql.Date(a.getTermino().getTime());
			ps.setDate(8, termino);
			ps.setBoolean(9, a.isTermoCompromisso());
			ps.setBoolean(10, a.isPlanoAtividade());
			ps.setBoolean(11, a.isRelatorio1());
			ps.setBoolean(12, a.isRelatorio2());
			ps.setBoolean(13, a.isRelatorio3());
			ps.setBoolean(14, a.isRelatorio4());
			ps.setBoolean(15, a.isTermoAditivo());
			ps.setBoolean(16, a.isAvAluno());
			ps.setBoolean(17, a.isAvEmpresa());
			ps.setBoolean(18, a.isTermoRecisao());
			ps.setBoolean(19, a.isTermoRealizacao());
			ps.setBoolean(20, a.isTermoContrato());
			ps.setBoolean(21, true);

			if (a.isObg()) {
				ps.setString(22, "Sim");
			} else {
				ps.setString(22, "N�o");
			}

			ps.setInt(23, a.getCargaHoraria());
			ps.executeUpdate();

			add = true;

		} catch (Exception e) {

			e.printStackTrace();

		} finally {
			ps.close();
		}

		return add;

	}

	@Override
	public List<Aluno> pesquisarNome(String nome) throws SQLException {

		List<Aluno> alunos = new ArrayList<Aluno>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		String sql = "SELECT *, date_add(inicio, interval 182 day) as rel1, date_add(inicio, interval 365 day) as rel2, date_add(inicio, interval 547 day) as rel3, date_add(inicio, interval 730 day) as rel4 from infoAlunos where nome like ? and ativo=true order by nome";
		try {

			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);

			ps.setString(1, "%" + nome + "%");

			rs = ps.executeQuery();

			while (rs.next()) {
				Aluno a = new Aluno();
				a.setRa(rs.getString("ra"));
				a.setNome(rs.getString("nome"));
				a.setEmail(rs.getString("email"));
				a.setEmpresa(rs.getString("empresa"));
				a.setId_curso(rs.getInt("id_curso"));
				a.setSenha(rs.getString("senha"));
				a.setInicio(rs.getDate("inicio"));
				a.setTermino(rs.getDate("termino"));
				a.setDtIni(sdf.format(rs.getDate("inicio")));
				a.setDtTer(sdf.format(rs.getDate("termino")));
				a.setTermoCompromisso(rs.getBoolean("termoCompromisso"));
				a.setPlanoAtividade(rs.getBoolean("planoAtividade"));
				a.setRelatorio1(rs.getBoolean("relatorio1"));
				a.setRelatorio2(rs.getBoolean("relatorio2"));
				a.setRelatorio3(rs.getBoolean("relatorio3"));
				a.setRelatorio4(rs.getBoolean("relatorio4"));
				a.setTermoAditivo(rs.getBoolean("termoAditivo"));
				a.setAvAluno(rs.getBoolean("avAluno"));
				a.setAvEmpresa(rs.getBoolean("avEmpresa"));
				a.setTermoRecisao(rs.getBoolean("termoRecisao"));
				a.setTermoRealizacao(rs.getBoolean("termoRealizacao"));
				a.setTermoContrato(rs.getBoolean("termoContrato"));
				a.setObrigatorio(rs.getString("obrigatorio"));
				a.setRel1(sdf.format(rs.getDate("rel1")));
				a.setRel2(sdf.format(rs.getDate("rel2")));
				a.setRel3(sdf.format(rs.getDate("rel3")));
				a.setRel4(sdf.format(rs.getDate("rel4")));

				if (a.getObrigatorio().equals("Sim")) {
					a.setObg(true);
				} else {
					a.setObg(false);
				}

				a.setCargaHoraria(rs.getInt("cargaHoraria"));

				alunos.add(a);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			ps.close();
		}

		return alunos;
	}

	@Override
	public Aluno pesquisar(String id, String senha) throws SQLException {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Aluno a = new Aluno();

		String sql = "SELECT *, date_add(inicio, interval 182 day) as rel1, date_add(inicio, interval 365 day) as rel2, date_add(inicio, interval 547 day) as rel3, date_add(inicio, interval 730 day) as rel4 from infoAlunos where ra = ? and senha like ? and ativo=true order by inicio desc";

		try {

			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			ps.setString(1, id);
			ps.setString(2, senha);

			rs = ps.executeQuery();

			if (rs.next()) {
				System.out.println("achou alguma coisa");
				a.setRa(rs.getString("ra"));
				a.setNome(rs.getString("nome"));
				a.setEmail(rs.getString("email"));
				a.setEmpresa(rs.getString("empresa"));
				a.setId_curso(rs.getInt("id_curso"));
				a.setSenha(rs.getString("senha"));
				a.setDtIni(sdf.format(rs.getDate("inicio")));
				a.setDtTer(sdf.format(rs.getDate("termino")));
				a.setInicio(rs.getDate("inicio"));
				a.setTermino(rs.getDate("termino"));
				a.setTermoCompromisso(rs.getBoolean("termoCompromisso"));
				a.setPlanoAtividade(rs.getBoolean("planoAtividade"));
				a.setRelatorio1(rs.getBoolean("relatorio1"));
				a.setRelatorio2(rs.getBoolean("relatorio2"));
				a.setRelatorio3(rs.getBoolean("relatorio3"));
				a.setRelatorio4(rs.getBoolean("relatorio4"));
				a.setTermoAditivo(rs.getBoolean("termoAditivo"));
				a.setAvAluno(rs.getBoolean("avAluno"));
				a.setAvEmpresa(rs.getBoolean("avEmpresa"));
				a.setTermoRecisao(rs.getBoolean("termoRecisao"));
				a.setTermoRealizacao(rs.getBoolean("termoRealizacao"));
				a.setTermoContrato(rs.getBoolean("termoContrato"));
				a.setObrigatorio(rs.getString("obrigatorio"));
				a.setRel1(sdf.format(rs.getDate("rel1")));
				a.setRel2(sdf.format(rs.getDate("rel2")));
				a.setRel3(sdf.format(rs.getDate("rel3")));
				a.setRel4(sdf.format(rs.getDate("rel4")));

				if (a.getObrigatorio().equals("Sim")) {
					a.setObg(true);
				} else {
					a.setObg(false);
				}

				a.setCargaHoraria(rs.getInt("cargaHoraria"));

			} else {
				a.setRa("");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			ps.close();
		}

		return a;
	}

	@Override
	public boolean editar(Aluno a) throws SQLException {
		boolean alt = false;
		String sql = "update infoAlunos set nome = ?, email = ?, empresa = ?, id_curso = ?, inicio = ?, termino = ?, termoCompromisso = ?, planoAtividade = ?, relatorio1 = ?, relatorio2 = ?, relatorio3 = ?, relatorio4 = ?, termoAditivo = ?, avAluno = ?, avEmpresa = ?, termoRecisao = ?, termoRealizacao = ?, termoContrato = ?, obrigatorio = ?, cargaHoraria = ? where ra like ? and inicio like ?";
		Connection con = null;
		PreparedStatement ps = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		try {
			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);
			ps.setString(1, a.getNome());
			ps.setString(2, a.getEmail());
			ps.setString(3, a.getEmpresa());
			ps.setInt(4, a.getId_curso());
			java.sql.Date inicio = new java.sql.Date(a.getInicio().getTime());
			ps.setDate(5, inicio);
			java.sql.Date termino = new java.sql.Date(a.getTermino().getTime());
			ps.setDate(6, termino);
			ps.setBoolean(7, a.isTermoCompromisso());
			ps.setBoolean(8, a.isPlanoAtividade());
			ps.setBoolean(9, a.isRelatorio1());
			ps.setBoolean(10, a.isRelatorio2());
			ps.setBoolean(11, a.isRelatorio3());
			ps.setBoolean(12, a.isRelatorio4());
			ps.setBoolean(13, a.isTermoAditivo());
			ps.setBoolean(14, a.isAvAluno());
			ps.setBoolean(15, a.isAvEmpresa());
			ps.setBoolean(16, a.isTermoRecisao());
			ps.setBoolean(17, a.isTermoRealizacao());
			ps.setBoolean(18, a.isTermoContrato());

			if (a.isObg()) {
				ps.setString(19, "Sim");
			} else {
				ps.setString(19, "N�o");
			}

			ps.setInt(20, a.getCargaHoraria());
			ps.setString(21, a.getRa());

			Date d = sdf.parse(a.getDtIni());
			java.sql.Date dtIni = new java.sql.Date(d.getTime());
			ps.setDate(22, dtIni);

			ps.executeUpdate();

			alt = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
		}

		return alt;
	}

	@Override
	public boolean excluir(String ra, Date inicio) throws SQLException {
		boolean exc = false;
		String sql = "update infoAlunos set ativo = false where ra like ? and inicio like ?";
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);

			ps.setString(1, ra);
			
			java.sql.Date ini = new java.sql.Date(inicio.getTime());
			ps.setDate(2, ini);


			ps.executeUpdate();
			exc = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
		}

		return exc;
	}
	
	@Override
	public boolean excluirEq(String ra, Date def) throws SQLException {
		boolean exc = false;
		String sql = "update infoAlunosEq set ativo = false where ra like ? and dtDeferimento like ?";
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);

			ps.setString(1, ra);
			
			java.sql.Date d = new java.sql.Date(def.getTime());
			ps.setDate(2, d);


			ps.executeUpdate();
			exc = true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
		}

		return exc;
	}

	@Override
	public List<Aluno> pesquisarRa(String ra) throws SQLException {
		
		List<Aluno> alunos = new ArrayList<Aluno>();

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT *, date_add(inicio, interval 182 day) as rel1, date_add(inicio, interval 365 day) as rel2, date_add(inicio, interval 547 day) as rel3, date_add(inicio, interval 730 day) as rel4 from infoAlunos where ra like ? and ativo = true order by nome";
		try {
			System.out.println("o ra enviado foi: " + ra);
			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			ps.setString(1, ra);

			rs = ps.executeQuery();

			while (rs.next()) {
				System.out.println("achou alguma coisa");
				Aluno a = new Aluno();
				a.setRa(ra);
				a.setNome(rs.getString("nome"));
				a.setEmail(rs.getString("email"));
				a.setEmpresa(rs.getString("empresa"));
				a.setId_curso(rs.getInt("id_curso"));
				a.setSenha(rs.getString("senha"));
				a.setInicio(rs.getDate("inicio"));
				a.setTermino(rs.getDate("termino"));
				a.setDtIni(sdf.format(rs.getDate("inicio")));
				a.setDtTer(sdf.format(rs.getDate("termino")));
				a.setTermoCompromisso(rs.getBoolean("termoCompromisso"));
				a.setPlanoAtividade(rs.getBoolean("planoAtividade"));
				a.setRelatorio1(rs.getBoolean("relatorio1"));
				a.setRelatorio2(rs.getBoolean("relatorio2"));
				a.setRelatorio3(rs.getBoolean("relatorio3"));
				a.setRelatorio4(rs.getBoolean("relatorio4"));
				a.setTermoAditivo(rs.getBoolean("termoAditivo"));
				a.setAvAluno(rs.getBoolean("avAluno"));
				a.setAvEmpresa(rs.getBoolean("avEmpresa"));
				a.setTermoRecisao(rs.getBoolean("termoRecisao"));
				a.setTermoRealizacao(rs.getBoolean("termoRealizacao"));
				a.setTermoContrato(rs.getBoolean("termoContrato"));
				a.setObrigatorio(rs.getString("obrigatorio"));
				a.setRel1(sdf.format(rs.getDate("rel1")));
				a.setRel2(sdf.format(rs.getDate("rel2")));
				a.setRel3(sdf.format(rs.getDate("rel3")));
				a.setRel4(sdf.format(rs.getDate("rel4")));

				if (a.getObrigatorio().equals("Sim")) {
					a.setObg(true);
				} else {
					a.setObg(false);
				}

				a.setCargaHoraria(rs.getInt("cargaHoraria"));
				
				alunos.add(a);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			ps.close();
		}

		return alunos;
	}

	@Override
	public boolean adicionarEq(Equivalencia e) throws SQLException {
		String sql = "Insert into infoAlunosEq (ra, nome, email, empresa, id_curso, senha, deferido, dtDeferimento, ativo)"
				+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection con = null;
		PreparedStatement ps = null;
		boolean add = false;

		try {
			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);

			System.out.println("Curso: " + e.getId_curso());

			ps.setString(1, e.getRa());
			ps.setString(2, e.getNome());
			ps.setString(3, e.getEmail());
			ps.setString(4, e.getEmpresa());
			ps.setInt(5, e.getId_curso());
			ps.setString(6, "123");
			ps.setBoolean(7, e.isDeferido());
			java.sql.Date dtDef = new java.sql.Date(e.getDtDeferimento().getTime());
			ps.setDate(8, dtDef);
			ps.setBoolean(9, true);

			ps.executeUpdate();

			add = true;

		} catch (Exception ex) {

			ex.printStackTrace();

		} finally {
			ps.close();
		}

		return add;
	}

	@Override
	public Equivalencia pesquisarEq(String id, String senha) throws SQLException {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Equivalencia eq = new Equivalencia();

		String sql = "SELECT * from infoAlunosEq where ra like ? and senha like ? and ativo = true order by nome";

		try {

			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			ps.setString(1, id);
			ps.setString(2, senha);

			rs = ps.executeQuery();

			if (rs.next()) {
				System.out.println("achou alguma coisa");
				eq.setRa(rs.getString("ra"));
				eq.setNome(rs.getString("nome"));
				eq.setEmail(rs.getString("email"));
				eq.setEmpresa(rs.getString("empresa"));
				eq.setId_curso(rs.getInt("id_curso"));
				eq.setSenha(rs.getString("senha"));
				eq.setDeferido(rs.getBoolean("deferido"));
				eq.setDtDeferimento(rs.getDate("dtDeferimento"));
				eq.setDataDef(sdf.format(rs.getDate("dtDeferimento")));

			} else {
				eq.setRa("");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			ps.close();
		}

		return eq;
	}

	@Override
	public List<Equivalencia> pesquisarNomeEq(String nome) throws SQLException {
		List<Equivalencia> alunosEq = new ArrayList<Equivalencia>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		String sql = "SELECT * from infoAlunosEq where nome like ? and ativo = true order by nome";
		try {

			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);

			ps.setString(1, "%" + nome + "%");

			rs = ps.executeQuery();

			while (rs.next()) {
				Equivalencia eq = new Equivalencia();
				eq.setRa(rs.getString("ra"));
				eq.setNome(rs.getString("nome"));
				eq.setEmail(rs.getString("email"));
				eq.setEmpresa(rs.getString("empresa"));
				eq.setId_curso(rs.getInt("id_curso"));
				eq.setDeferido(rs.getBoolean("deferido"));
				eq.setDataDef(sdf.format(rs.getDate("dtDeferimento")));
				eq.setDtDeferimento(rs.getDate("dtDeferimento"));
				alunosEq.add(eq);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			ps.close();
		}

		return alunosEq;
	}

	@Override
	public List<Equivalencia> pesquisarRaEq(String ra) throws SQLException {
		
		List<Equivalencia> equivalencias = new ArrayList<Equivalencia>();

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT * from infoAlunosEq where ra = ? and ativo = true order by nome";
		try {
			System.out.println("o ra enviado foi: " + ra);
			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);

			ps.setString(1, ra);

			rs = ps.executeQuery();

			while (rs.next()) {
				
				Equivalencia eq = new Equivalencia();
				System.out.println("achou alguma coisa");
				eq.setRa(ra);
				eq.setNome(rs.getString("nome"));
				eq.setEmail(rs.getString("email"));
				eq.setEmpresa(rs.getString("empresa"));
				eq.setId_curso(rs.getInt("id_curso"));
				eq.setSenha(rs.getString("senha"));
				eq.setDtDeferimento(rs.getDate("dtDeferimento"));
				eq.setDeferido(rs.getBoolean("deferido"));
				
				equivalencias.add(eq);
			} 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			ps.close();
		}

		return equivalencias;
	}

	@Override
	public boolean editarEq(Equivalencia eq) throws SQLException {
		boolean alt = false;
		String sql = "update infoAlunosEq set nome = ?, email = ?, empresa = ?, id_curso = ?, deferido = ?, dtDeferimento = ? where ra like ?";
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);
			ps.setString(1, eq.getNome());
			ps.setString(2, eq.getEmail());
			ps.setString(3, eq.getEmpresa());
			ps.setInt(4, eq.getId_curso());
			ps.setBoolean(5, eq.isDeferido());
			java.sql.Date inicio = new java.sql.Date(eq.getDtDeferimento().getTime());
			ps.setDate(6, inicio);
			ps.setString(7, eq.getRa());

			ps.executeUpdate();

			alt = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ps.close();
		}

		return alt;
	}

	@Override
	public boolean estagioAtivo(String ra) throws SQLException {
		boolean ok = false;

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String sql = "SELECT * from infoAlunos where ra = ? and termoRecisao = false and ativo = true";

		try {

			con = DBResourceManager.getInstance().getCon();
			ps = con.prepareStatement(sql);

			ps.setString(1, ra);

			rs = ps.executeQuery();

			if (rs.next()) {
				ok = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rs.close();
			ps.close();
		}

		return ok;
	}

}
