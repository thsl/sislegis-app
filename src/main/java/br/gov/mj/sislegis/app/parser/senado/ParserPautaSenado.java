package br.gov.mj.sislegis.app.parser.senado;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import br.gov.mj.sislegis.app.enumerated.Origem;
import br.gov.mj.sislegis.app.model.Proposicao;

import com.thoughtworks.xstream.XStream;

public class ParserPautaSenado {
	
	public static void main(String[] args) throws Exception {
		ParserPautaSenado parser = new ParserPautaSenado();
		
		// TODO: Informação que vem do filtro
		String siglaComissao = "CAE";
		String datIni = "20140801";
		System.out.println(parser.getProposicoes(siglaComissao, datIni).toString());
	}
	
	public List<Proposicao> getProposicoes(String siglaComissao, String datIni) throws Exception {
		List<Proposicao> proposicoes = new ArrayList<Proposicao>();
			
		XStream xstreamReuniao = new XStream();
		xstreamReuniao.ignoreUnknownElements();
		
		configReuniao(xstreamReuniao);

		for (ReuniaoBean bean : getReunioes(siglaComissao, datIni)) {
			
			String wsURLReuniao = "http://legis.senado.leg.br/dadosabertos/reuniao/"+bean.getCodigo();
			URL url = new URL(wsURLReuniao);
			ReuniaoBean reuniao = new ReuniaoBean();
			
			xstreamReuniao.fromXML(url, reuniao);

			proposicoes.addAll(reuniao.getProposicoes());
		}
		
		return proposicoes;
	}
	
	private List<ReuniaoBean> getReunioes(String siglaComissao, String datIni) throws Exception {
		String wsURL = "http://legis.senado.leg.br/dadosabertos/agenda/"+datIni+"?colegiado="+siglaComissao;
		URL url = new URL(wsURL);
		
		XStream xstreamAgenda = new XStream();
		xstreamAgenda.ignoreUnknownElements();
		
		ListaReunioes reunioes = new ListaReunioes();
		
		configAgenda(xstreamAgenda);

		xstreamAgenda.fromXML(url, reunioes);
	
		return reunioes.getReunioes();
	}
	
	private void configAgenda(XStream xstream) {
		xstream.alias("Reunioes", ListaReunioes.class);
		xstream.alias("Reuniao", ReuniaoBean.class);
		
		xstream.addImplicitCollection(ListaReunioes.class, "reunioes");
		
		xstream.aliasField("Codigo", ReuniaoBean.class, "codigo");
	}
	
	private void configReuniao(XStream xstream) {
		xstream.alias("Reuniao", ReuniaoBean.class);
		xstream.alias("Comissao", ComissaoBean.class);
		xstream.alias("Parte", ParteBean.class);
		xstream.alias("Item", ItemBean.class);
		xstream.alias("Materia", Proposicao.class);
		
		xstream.aliasField("Partes", ReuniaoBean.class, "partes");
		xstream.aliasField("Comissoes", ReuniaoBean.class, "comissoes");
		xstream.aliasField("Codigo", ReuniaoBean.class, "codigo");
		xstream.aliasField("Sigla", ComissaoBean.class, "sigla");
		xstream.aliasField("Nome", ComissaoBean.class, "nome");
		xstream.aliasField("Itens", ParteBean.class, "itens");
		xstream.aliasField("Materia", ItemBean.class, "proposicao");
		xstream.aliasField("SeqOrdemPauta", ItemBean.class, "seqOrdemPauta");
		xstream.aliasField("Codigo", Proposicao.class, "idProposicao");
		xstream.aliasField("Subtipo", Proposicao.class, "tipo");
		xstream.aliasField("Numero", Proposicao.class, "numero");
		xstream.aliasField("Ano", Proposicao.class, "ano");
		xstream.aliasField("Ementa", Proposicao.class, "ementa");
	}
}

class ListaReunioes {
	protected List<ReuniaoBean> reunioes = new ArrayList<ReuniaoBean>();

	protected List<ReuniaoBean> getReunioes() {
		return reunioes;
	}
}

class ReuniaoBean {
	protected Integer codigo;
	protected List<ComissaoBean> comissoes = new ArrayList<ComissaoBean>();
	protected List<ParteBean> partes = new ArrayList<ParteBean>();
	
	protected Integer getCodigo() {
		return codigo;
	}
	
	protected List<ParteBean> getPartes() {
		return partes;
	}
	
	protected List<ComissaoBean> getComissoes() {
		return comissoes;
	}

	protected List<Proposicao> getProposicoes() {
		List<Proposicao> materias = new ArrayList<Proposicao>();
		
		for (ParteBean parteBean : this.getPartes()) {
			List<ItemBean> itens = parteBean.getItens();
		
			for (ItemBean itemBean : itens) {
				itemBean.getProposicao().setComissao(comissoes.get(0).getSigla() + " - " + comissoes.get(0).getNome());
				itemBean.getProposicao().setOrigem(Origem.SENADO);
				itemBean.getProposicao().setLinkProposicao("http://www.senado.leg.br/atividade/materia/detalhes.asp?p_cod_mate="+itemBean.getProposicao().getIdProposicao());
				itemBean.getProposicao().setLinkPauta("http://legis.senado.leg.br/comissoes/reuniao?reuniao="+getCodigo());
				materias.add(itemBean.getProposicao());
			}
		}
		
		return materias;
	}
}

class ComissaoBean {
	protected String sigla;
	protected String nome;
	
	protected String getSigla() {
		return sigla;
	}
	protected String getNome() {
		return nome;
	}
}

class ParteBean {
	protected List<ItemBean> itens = new ArrayList<ItemBean>();

	protected List<ItemBean> getItens() {
		return itens;
	}

	protected void setItens(List<ItemBean> itens) {
		this.itens = itens;
	}
}

class ItemBean {
	protected Integer seqOrdemPauta;
	protected Proposicao proposicao;

	protected Proposicao getProposicao() {
		proposicao.setSeqOrdemPauta(seqOrdemPauta);
		return proposicao;
	}

	protected Integer getSeqOrdemPauta() {
		return seqOrdemPauta;
	}
}