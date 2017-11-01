package bean;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import dao.AdminDao;
import dao.AlunoDao;
import dao.CursoDao;
import dao.IAdminDao;
import dao.IAlunoDao;
import dao.ICursoDao;
import dao.ILoginDao;
import dao.LoginDao;
import model.Administrador;
import model.Aluno;
import model.Curso;
import model.Equivalencia;

@ManagedBean
@SessionScoped
public class AlunoMB {

	private String image;
	private boolean logado = false;
	private boolean admin = false;
	private Aluno aluno;
	private Equivalencia equivalencia;
	private Administrador administrador;
	private Curso curso;
	private List<Aluno> lstAlunos = new ArrayList<Aluno>();
	private List<Curso> lstCursos = new ArrayList<Curso>();
	private List<Equivalencia> lstEquivalencia = new ArrayList<Equivalencia>();
	private IAlunoDao alunoDao = new AlunoDao();
	private String pesqNome;
	private String pesqRA;
	private String opcPesquisa;
	private int totalHoras;

	// Campos de imagem
	private String termoCompromisso;
	private String planoAtividade;
	private String relatorio1;
	private String relatorio2;
	private String relatorio3;
	private String relatorio4;
	private String termoAditivo;
	private String avAluno;
	private String avEmpresa;
	private String termoRecisao;
	private String termoRealizacao;
	private String termoContrato;
	private String deferido;

	@PostConstruct
	public void inicializar() {
		aluno = new Aluno();
		equivalencia = new Equivalencia();
		administrador = new Administrador();
		curso = new Curso();

		image = "";
		try {
			carregarCursos();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("nao carregou os cursos");
			e.printStackTrace();
		}

		// image = "img/correct.png";
		// image2 = "img/incorrect.png";
	}

	// public void refresh() {
	// FacesContext context = FacesContext.getCurrentInstance();
	// Application application = context.getApplication();
	// ViewHandler viewHandler = application.getViewHandler();
	// UIViewRoot viewRoot = viewHandler.createView(context,
	// context.getViewRoot().getViewId());
	// context.setViewRoot(viewRoot);
	// context.renderResponse();
	// }

	public String logar() throws SQLException {
		String pagina = "";
		ILoginDao loginDao = new LoginDao();
		IAdminDao adminDao = new AdminDao();

		if (adminDao.autenticarLogin(administrador.getUsuario(), administrador.getSenha())) {
			System.out.println("logou admin");
			logado = true;
			admin = true;
			pagina = "areaAdmin?faces-redirect=true";

		} else if (loginDao.autenticarLogin(aluno.getRa(), aluno.getSenha())) {
			System.out.println("logou aluno");
			logado = true;

			aluno = alunoDao.pesquisar(aluno.getRa(), aluno.getSenha());

			if (!aluno.getRa().equals("")) {
				carregarImagens();

				try {
					contarHoras(aluno);
				} catch (ParseException e) {
					e.printStackTrace();
				}

				datasLimite();
				pagina = "dadosAluno?faces-redirect=true";

			} else {
				logado = false;
				pagina = "index?faces-redirect=true";
			}

		} else if (loginDao.autenticarLoginEq(aluno.getRa(), aluno.getSenha())) {
			System.out.println("logou aluno eq");
			logado = true;

			equivalencia = alunoDao.pesquisarEq(aluno.getRa(), aluno.getSenha());

			if (!equivalencia.getRa().equals("")) {
				carregarImagensEq();
				pagina = "dadosEquivalencia?faces-redirect=true";
				aluno.setRa("");
				aluno.setSenha("");

			} else {
				logado = false;
				pagina = "index?faces-redirect=true";
			}

		} else {
			System.out.println("nao logou ninguem e a p�gina �: " + pagina + "!");
			logado = false;
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("formBody",
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "Falha de autentica��o!", "RA ou senha incorretos!"));
			// fc.addMessage("formBody:txtSenha", new
			// FacesMessage(FacesMessage.SEVERITY_WARN,"",""));

		}

		return pagina;
	}

	public String deslogar() {
		HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		sessao.invalidate();

		return "index?faces-redirect=true";
	}

	public String adicionar() throws SQLException {
		String pagina = "";

		aluno.setId_curso(curso.getId_curso());

		if (validarDados()) {
			if (alunoDao.estagioAtivo(aluno.getRa())) {
				
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN,
						"N�o foi poss�vel registrar o aluno", "� permitido somente um registro de est�gio ativo!"));

			} else {

				if (alunoDao.adicionar(aluno)) {
				
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage("formBody",
							new FacesMessage(FacesMessage.SEVERITY_INFO, "Aluno registrado com sucesso!", ""));
					limpar();
					
				} else {
					
					FacesContext fc = FacesContext.getCurrentInstance();
					fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
							"Falha ao registrar o aluno", "Verifique os campos e tente novamente"));
					
				}
			}
		}

		return pagina;
	}

	public String adicionarEq() throws SQLException {
		String pagina = "";

		// equivalencia.setId_curso(curso.getId_curso());
		if (validarDadosEq()) {
			if (alunoDao.adicionarEq(equivalencia)) {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody",
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Aluno registrado com sucesso!", ""));
				limpar();
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL, "Falha ao registrar o aluno",
						"Verifique os campos e tente novamente"));
			}
		}

		return pagina;
	}

	public void limpar() {
		aluno = new Aluno();
		equivalencia = new Equivalencia();
	}

	public String doLogout() {
		HttpSession sessao = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
		sessao.invalidate();

		return "login?faces-redirect=true";

	}

	public void carregarImagensEq() {
		if (equivalencia.isDeferido()) {
			deferido = "img/correct.png";
		} else {
			deferido = "img/incorrect.png";
		}
	}

	public void carregarImagens() {
		if (aluno.isTermoCompromisso()) {
			termoCompromisso = "img/correct.png";
		} else {
			termoCompromisso = "img/incorrect.png";
		}

		if (aluno.isPlanoAtividade()) {
			planoAtividade = "img/correct.png";
		} else {
			planoAtividade = "img/incorrect.png";
		}

		if (aluno.isRelatorio1()) {
			relatorio1 = "img/correct.png";
		} else {
			relatorio1 = "img/incorrect.png";
		}

		if (aluno.isRelatorio2()) {
			relatorio2 = "img/correct.png";
		} else {
			relatorio2 = "img/incorrect.png";
		}

		if (aluno.isRelatorio3()) {
			relatorio3 = "img/correct.png";
		} else {
			relatorio3 = "img/incorrect.png";
		}

		if (aluno.isRelatorio4()) {
			relatorio4 = "img/correct.png";
		} else {
			relatorio4 = "img/incorrect.png";
		}

		if (aluno.isTermoAditivo()) {
			termoAditivo = "img/correct.png";
		} else {
			termoAditivo = "img/incorrect.png";
		}

		if (aluno.isAvAluno()) {
			avAluno = "img/correct.png";
		} else {
			avAluno = "img/incorrect.png";
		}

		if (aluno.isAvEmpresa()) {
			avEmpresa = "img/correct.png";
		} else {
			avEmpresa = "img/incorrect.png";
		}

		if (aluno.isTermoRecisao()) {
			termoRecisao = "img/correct.png";
		} else {
			termoRecisao = "img/incorrect.png";
		}

		if (aluno.isTermoRealizacao()) {
			termoRealizacao = "img/correct.png";
		} else {
			termoRealizacao = "img/incorrect.png";
		}

		if (aluno.isTermoContrato()) {
			termoContrato = "img/correct.png";
		} else {
			termoContrato = "img/incorrect.png";
		}

		System.out.println("carregou tudo!");

	}

	public void pesquisarNome() throws SQLException {
		lstAlunos = alunoDao.pesquisarNome(pesqNome);
		lstEquivalencia = alunoDao.pesquisarNomeEq(pesqNome);

		if (lstEquivalencia.isEmpty() && lstAlunos.isEmpty()) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN, "Nenhum aluno encontrado",
					"Confira o nome e tente novamente"));
		}

	}

	public void pesquisarRA() throws SQLException {
//		lstAlunos.clear();
//		lstEquivalencia.clear();
		lstAlunos = alunoDao.pesquisarRa(pesqRA);
		lstEquivalencia = alunoDao.pesquisarRaEq(pesqRA);

		// if (aluno.getRa() == 0) {
		// System.out.println("n�o foram encontrados alunos com o ra");
		// FacesContext fc = FacesContext.getCurrentInstance();
		// fc.addMessage("formBody", new
		// FacesMessage(FacesMessage.SEVERITY_WARN, "Nenhum aluno encontrado",
		// "Confira o RA e tente novamente"));
		// }
//		lstAlunos.add(aluno);
//		lstEquivalencia.add(equivalencia);

		if (lstEquivalencia.isEmpty() && lstAlunos.isEmpty()) {
			FacesContext fc = FacesContext.getCurrentInstance();
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN, "Nenhum aluno encontrado",
					"Confira o RA e tente novamente"));
		}
	}

	public void carregarCursos() throws SQLException {
		ICursoDao cursoDao = new CursoDao();
		lstCursos = cursoDao.listarCursos();

	}

	public String editarAluno(Aluno a) {

		aluno = a;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		a.setDtIni(sdf.format(a.getInicio()));

		return "editarAluno?faces-redirect=true";
	}

	public String editarEquivalencia(Equivalencia eq) {
		equivalencia = eq;

		return "editarEquivalencia?faces-redirect=true";
	}

	public String excluirAluno(Aluno a) {
		aluno = a;
		carregarImagens();
		return "excluirAluno?faces-redirect=true";
	}

	public String excluirEquivalencia(Equivalencia eq) {
		equivalencia = eq;
		carregarImagensEq();
		return "excluirEquivalencia?faces-redirect=true";
	}

	public String redirectAdmin() {
		lstAlunos.clear();
		aluno = new Aluno();
		equivalencia = new Equivalencia();
		lstEquivalencia.clear();

		return "consultarAluno?faces-redirect=true";
	}

	public String salvarEdit() throws SQLException {
		IAlunoDao alunoDao = new AlunoDao();
		String pagina = "";

		if (validarDados()) {
			if (alunoDao.editar(aluno)) {
				aluno = new Aluno();
				lstAlunos.clear();
				equivalencia = new Equivalencia();
				lstEquivalencia.clear();
				pagina = "consultarAluno?faces-redirect=true";
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN,
						"N�o foi poss�vel atualizar as informa��es!", "Confira os dados e tente novamente."));
			}
		}
		return pagina;
	}

	public String salvarEditEq() throws SQLException {
		IAlunoDao alunoDao = new AlunoDao();
		String pagina = "";

		if (validarDadosEq()) {
			if (alunoDao.editarEq(equivalencia)) {
				aluno = new Aluno();
				lstAlunos.clear();
				equivalencia = new Equivalencia();
				lstEquivalencia.clear();
				pagina = "consultarAluno?faces-redirect=true";
			} else {
				FacesContext fc = FacesContext.getCurrentInstance();
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_WARN,
						"N�o foi poss�vel atualizar as informa��es!", "Confira os dados e tente novamente."));
			}
		}
		return pagina;
	}

	public String salvarExc() throws SQLException {
		String pagina = "";
		IAlunoDao alunoDao = new AlunoDao();

		if (alunoDao.excluir(aluno.getRa(), aluno.getInicio())) {
			aluno = new Aluno();
			equivalencia = new Equivalencia();
			lstEquivalencia.clear();
			lstAlunos.clear();
			pagina = "consultarAluno?faces-redirect=true";
		}

		return pagina;
	}

	public String salvarExcEq() throws SQLException {
		String pagina = "";
		IAlunoDao alunoDao = new AlunoDao();

		if (alunoDao.excluirEq(equivalencia.getRa(), equivalencia.getDtDeferimento())) {
			aluno = new Aluno();
			equivalencia = new Equivalencia();
			lstEquivalencia.clear();
			lstAlunos.clear();
			pagina = "consultarAluno?faces-redirect=true";
		}

		return pagina;
	}

	public String visualizarAluno(Aluno a) {
		aluno = a;
		carregarImagens();
		try {
			contarHoras(aluno);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		datasLimite();
		return "visualizarAluno?faces-redirect=true";
	}

	public String visualizarEquivalencia(Equivalencia eq) {
		equivalencia = eq;
		carregarImagensEq();
		return "visualizarEquivalencia?faces-redirect=true";
	}

	public void contarHoras(Aluno a) throws ParseException {
		float horas = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String ini = sdf.format(a.getInicio());
		String fim = sdf.format(a.getTermino());

		Calendar inicio = Calendar.getInstance();
		Calendar termino = Calendar.getInstance();

		inicio.setTime(sdf.parse(ini));
		termino.setTime(sdf.parse(fim));
		// System.out.println(data1 + " data 1");

		long dt = (termino.getTimeInMillis() - inicio.getTimeInMillis());
		float dias = (dt / 86400000L);

		// int dias = termino.get(Calendar.DAY_OF_YEAR) -
		// inicio.get(Calendar.DAY_OF_YEAR);
		// System.out.println((String.valueOf(dias))+" dias");

		float sem = (dias / 7);
		System.out.println(sem + " semanas");

		horas = (sem * a.getCargaHoraria());
		System.out.println(horas + " horas" + " - " + a.getCargaHoraria() + " carga");

		setTotalHoras((int) horas);
	}

	public void datasLimite() {

		String re1 = aluno.getRel1();
		String re2 = aluno.getRel2();
		String re3 = aluno.getRel3();
		String re4 = aluno.getRel4();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			Date termino = aluno.getTermino();
			Date r1 = sdf.parse(re1);
			Date r2 = sdf.parse(re2);
			Date r3 = sdf.parse(re3);
			Date r4 = sdf.parse(re4);

			if (r1.after(termino) || r1.equals(termino)) {
				aluno.setRel1(sdf.format(termino));
				aluno.setRel2("");
				aluno.setRel3("");
				aluno.setRel4("");

			} else if (r2.after(termino) || r2.equals(termino)) {
				aluno.setRel2(sdf.format(termino));
				aluno.setRel3("");
				aluno.setRel4("");
			} else if (r3.after(termino) || r3.equals(termino)) {
				aluno.setRel3(sdf.format(termino));
				aluno.setRel4("");
			} else if (r4.after(termino) || r4.equals(termino)) {
				aluno.setRel4(sdf.format(termino));
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public boolean validarDados() {
		boolean ok = true;

		FacesContext fc = FacesContext.getCurrentInstance();

		// VALIDAR DATA
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String ini = sdf.format(aluno.getInicio());
		String fim = sdf.format(aluno.getTermino());

		Calendar inicio = Calendar.getInstance();
		Calendar termino = Calendar.getInstance();

		try {
			inicio.setTime(sdf.parse(ini));
			termino.setTime(sdf.parse(fim));
			if (inicio.after(termino)) {
				ok = false;
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
						"N�o foi poss�vel registrar o aluno!", "Data de t�rmino deve ser posterior a date de in�cio"));
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// VALIDAR RA
		try {
			Float.parseFloat(aluno.getRa());
		} catch (Exception e) {
			ok = false;
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"N�o foi poss�vel registrar o aluno!", "RA deve conter apenas n�meros"));
		}

		// VALIDAR NOME
		// try {
		// Float.parseFloat(aluno.getNome());
		// } catch (Exception e) {
		// ok = false;
		// fc.addMessage("formBody", new
		// FacesMessage(FacesMessage.SEVERITY_FATAL,
		// "N�o foi poss�vel registrar o aluno!", "Nome n�o pode conter
		// n�meros"));
		// }

		// VALIDAR CARGA HORARIA
		try {
			@SuppressWarnings("unused")
			float f = aluno.getCargaHoraria() / 2;
			if (aluno.getCargaHoraria() == 0) {
				ok = false;
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
						"N�o foi poss�vel registrar o aluno!", "Carga Hor�ria n�o pode ser igual a zero"));
			}
		} catch (Exception e) {
			ok = false;
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"N�o foi poss�vel registrar o aluno!", "Carga Hor�ria deve conter apenas n�meros"));
		}

		// VALIDAR EMAIL
		if (!aluno.getEmail().contains("@")) {
			ok = false;
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"N�o foi poss�vel registrar o aluno!", "E-mail inv�lido"));
		}

		return ok;

	}

	public boolean validarDadosEq() {
		boolean ok = true;

		FacesContext fc = FacesContext.getCurrentInstance();

		// VALIDAR DATA
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String dtEq = sdf.format(equivalencia.getDtDeferimento());
		String hj = sdf.format(new Date());

		Calendar dataEq = Calendar.getInstance();
		Calendar hoje = Calendar.getInstance();

		try {
			dataEq.setTime(sdf.parse(dtEq));
			hoje.setTime(sdf.parse(hj));
			if (dataEq.after(hoje)) {
				ok = false;
				fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
						"N�o foi poss�vel registrar o aluno!", "Equival�ncia n�o pode ter uma data futura"));
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// VALIDAR RA
		try {
			Float.parseFloat(equivalencia.getRa());
		} catch (Exception e) {
			ok = false;
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"N�o foi poss�vel registrar o aluno!", "RA deve conter apenas n�meros"));
		}

		// VALIDAR EMAIL
		if (!equivalencia.getEmail().contains("@")) {
			ok = false;
			fc.addMessage("formBody", new FacesMessage(FacesMessage.SEVERITY_FATAL,
					"N�o foi poss�vel registrar o aluno!", "E-mail inv�lido"));
		}

		return ok;

	}
	// GET E SET

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isLogado() {
		return logado;
	}

	public void setLogado(boolean logado) {
		this.logado = logado;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public Aluno getAluno() {
		return aluno;
	}

	public void setAluno(Aluno aluno) {
		this.aluno = aluno;
	}

	public Equivalencia getEquivalencia() {
		return equivalencia;
	}

	public void setEquivalencia(Equivalencia equivalencia) {
		this.equivalencia = equivalencia;
	}

	public Administrador getAdministrador() {
		return administrador;
	}

	public void setAdministrador(Administrador administrador) {
		this.administrador = administrador;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public List<Aluno> getLstAlunos() {
		return lstAlunos;
	}

	public void setLstAlunos(List<Aluno> lstAlunos) {
		this.lstAlunos = lstAlunos;
	}

	public List<Curso> getLstCursos() {
		return lstCursos;
	}

	public void setLstCursos(List<Curso> lstCursos) {
		this.lstCursos = lstCursos;
	}

	public List<Equivalencia> getLstEquivalencia() {
		return lstEquivalencia;
	}

	public void setLstEquivalencia(List<Equivalencia> lstEquivalencia) {
		this.lstEquivalencia = lstEquivalencia;
	}

	public IAlunoDao getAlunoDao() {
		return alunoDao;
	}

	public void setAlunoDao(IAlunoDao alunoDao) {
		this.alunoDao = alunoDao;
	}

	public String getTermoCompromisso() {
		return termoCompromisso;
	}

	public void setTermoCompromisso(String termoCompromisso) {
		this.termoCompromisso = termoCompromisso;
	}

	public String getPlanoAtividade() {
		return planoAtividade;
	}

	public void setPlanoAtividade(String planoAtividade) {
		this.planoAtividade = planoAtividade;
	}

	public String getRelatorio1() {
		return relatorio1;
	}

	public void setRelatorio1(String relatorio1) {
		this.relatorio1 = relatorio1;
	}

	public String getRelatorio2() {
		return relatorio2;
	}

	public void setRelatorio2(String relatorio2) {
		this.relatorio2 = relatorio2;
	}

	public String getRelatorio3() {
		return relatorio3;
	}

	public void setRelatorio3(String relatorio3) {
		this.relatorio3 = relatorio3;
	}

	public String getRelatorio4() {
		return relatorio4;
	}

	public void setRelatorio4(String relatorio4) {
		this.relatorio4 = relatorio4;
	}

	public String getTermoAditivo() {
		return termoAditivo;
	}

	public void setTermoAditivo(String termoAditivo) {
		this.termoAditivo = termoAditivo;
	}

	public String getAvAluno() {
		return avAluno;
	}

	public void setAvAluno(String avAluno) {
		this.avAluno = avAluno;
	}

	public String getAvEmpresa() {
		return avEmpresa;
	}

	public void setAvEmpresa(String avEmpresa) {
		this.avEmpresa = avEmpresa;
	}

	public String getTermoRecisao() {
		return termoRecisao;
	}

	public void setTermoRecisao(String termoRecisao) {
		this.termoRecisao = termoRecisao;
	}

	public String getTermoRealizacao() {
		return termoRealizacao;
	}

	public void setTermoRealizacao(String termoRealizacao) {
		this.termoRealizacao = termoRealizacao;
	}

	public String getTermoContrato() {
		return termoContrato;
	}

	public void setTermoContrato(String termoContrato) {
		this.termoContrato = termoContrato;
	}

	public String getDeferido() {
		return deferido;
	}

	public void setDeferido(String deferido) {
		this.deferido = deferido;
	}

	public String getPesqNome() {
		return pesqNome;
	}

	public void setPesqNome(String pesqNome) {
		this.pesqNome = pesqNome;
	}

	public String getPesqRA() {
		return pesqRA;
	}

	public void setPesqRA(String pesqRA) {
		this.pesqRA = pesqRA;
	}

	public String getOpcPesquisa() {
		return opcPesquisa;
	}

	public void setOpcPesquisa(String opcPesquisa) {
		this.opcPesquisa = opcPesquisa;
	}

	public int getTotalHoras() {
		return totalHoras;
	}

	public void setTotalHoras(int totalHoras) {
		this.totalHoras = totalHoras;
	}

}
