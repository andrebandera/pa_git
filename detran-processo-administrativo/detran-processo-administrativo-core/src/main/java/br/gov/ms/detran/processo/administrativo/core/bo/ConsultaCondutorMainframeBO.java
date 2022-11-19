/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.core.bo;

import static br.gov.ms.detran.comum.constantes.CodigoGrupoServico.HABILITACAO;
import br.gov.ms.detran.comum.entidade.enums.TransacaoEnum;
import br.gov.ms.detran.comum.iface.bca.IBeanIntegracao;
import br.gov.ms.detran.comum.iface.bca.ICampoLayout;
import br.gov.ms.detran.comum.iface.bca.IIntegracaoService;
import br.gov.ms.detran.comum.iface.bca.IResultadoIntegracao;
import br.gov.ms.detran.comum.projeto.integracao.bca.LogTransacaoBCAWrapper;
import br.gov.ms.detran.comum.projeto.util.ParametroEnvioIntegracao;
import static br.gov.ms.detran.comum.util.DetranCollectionUtil.ehNuloOuVazio;
import br.gov.ms.detran.comum.util.ServiceJndiLocator;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.integracao.comum.util.BCAEventoFilter;
import br.gov.ms.detran.integracao.comum.util.BeanIntegracaoBuilder;
import br.gov.ms.detran.integracao.comum.wrapper.EnvioTransacaoWrapper;
import br.gov.ms.detran.integracao.comum.wrapper.Transacao575EvB;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Christiano Carrilho.
 */
public class ConsultaCondutorMainframeBO {

    public ConsultaCondutorMainframeBO() {
    }

    /**
     * Retorna o motivo da cassação da CNH do condutor executando a transação 575
     * e verificando se existe algum bloqueio.
     * 
     * @param cpf Número do CPF do condutor.
     * @return Motivo da cassação.
     * @throws AppException 
     */
    public String getMotivoCassacao(String cpf) throws AppException {

        IIntegracaoService integracaoService = ServiceJndiLocator.<IIntegracaoService>lookup("ejb/IntegracaoService");
        IResultadoIntegracao resultado = 
            integracaoService.executarTransacao(integracaoService.executarTransacao(
                new EnvioTransacaoWrapper(
                        TransacaoEnum.TRAN_575,
                        HABILITACAO, 
                        "BR",
                        new ParametroEnvioIntegracao()
                        .adicionarParametro(7)
                        .adicionarParametro(cpf)
                        .adicionarParametro(0),
                        new LogTransacaoBCAWrapper())
                )
            );

        Map<String, List<ICampoLayout>> bloqueiosMainframe = resultado.getEventos("B");
        BeanIntegracaoBuilder bibuilder = new BeanIntegracaoBuilder(Transacao575EvB.class);
        List<IBeanIntegracao> beansBloqueios = bibuilder.setResultado(bloqueiosMainframe).build().getBeans();

        if (ehNuloOuVazio(beansBloqueios)) return null;

        BCAEventoFilter filter = new BCAEventoFilter(beansBloqueios);
        List<IBeanIntegracao> bloqueiosCassacao = filter
                .setPropertyFilter("tipo-atualizacao", "I", "S")
                .filter();

        if (ehNuloOuVazio(bloqueiosCassacao)) return null;

        StringBuilder motivo = new StringBuilder();

        for (IBeanIntegracao bean : bloqueiosCassacao) {
            Transacao575EvB eb = (Transacao575EvB) bean;
            motivo.append(eb.getMotivoBloqueio() != null ? eb.getMotivoBloqueio().trim() : "");
            motivo.append(" - ");
            motivo.append(eb.getDescricaoBloqueio() != null ? eb.getDescricaoBloqueio().trim() : "");
            motivo.append("; ");
        }

        return motivo.substring(0, motivo.length() -2);
    }
}