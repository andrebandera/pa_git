/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.gov.ms.detran.processo.administrativo.dto;

import br.gov.ms.detran.comum.entidade.enums.adm.SexoEnum;
import br.gov.ms.detran.comum.entidadesistema.enums.adm.PessoaOrigemEnum;
import br.gov.ms.detran.comum.entidadesistema.enums.adm.TipoPessoaEnum;
import br.gov.ms.detran.comum.entidadesistema.jpa.adm.Nome;
import br.gov.ms.detran.comum.entidadesistema.jpa.adm.Origem;
import br.gov.ms.detran.comum.entidadesistema.jpa.adm.PessoaJuridica;
import br.gov.ms.detran.comum.entidadesistema.jpa.adm.PessoaNomeGrupoServico;
import br.gov.ms.detran.comum.entidadesistema.wrapper.adm.DocumentoPessoaWrapper;
import br.gov.ms.detran.comum.entidadesistema.wrapper.adm.DocumentoWrapper;
import br.gov.ms.detran.comum.entidadesistema.wrapper.adm.NomePessoaJuridicaWrapper;
import br.gov.ms.detran.comum.entidadesistema.wrapper.adm.NomeWrapper2;
import br.gov.ms.detran.comum.entidadesistema.wrapper.adm.OrigemWrapper2;
import br.gov.ms.detran.comum.entidadesistema.wrapper.adm.PessoaFisicaWrapper2;
import br.gov.ms.detran.comum.entidadesistema.wrapper.adm.PessoaJuridicaWrapper2;
import br.gov.ms.detran.comum.entidadesistema.wrapper.adm.PessoaWrapper2;
import br.gov.ms.detran.comum.projeto.constantes.CodigoGrupoServico;
import br.gov.ms.detran.comum.projeto.constantes.DtnIntraPessoaParams;
import br.gov.ms.detran.comum.projeto.intranet.DetranWebIntranet;
import static br.gov.ms.detran.comum.projeto.util.DetranWebUtils.applicationMessageException;
import br.gov.ms.detran.comum.util.JsonJacksonUtils;
import br.gov.ms.detran.comum.util.Utils;
import br.gov.ms.detran.comum.util.exception.AppException;
import br.gov.ms.detran.comum.util.exception.ExceptionGeneric;
import br.gov.ms.detran.comum.util.logger.Logger;
import java.io.IOException;

/**
 *
 * @author Christiano Carrilho.
 */
public class DtnIntraPJUPessoa {

    private static final Logger LOGGER = Logger.getLogger(DtnIntraPJUPessoa.class);

    public DtnIntraPJUPessoa() {
    }

    /**
     *
     * @param cpf
     * @param nomeCondutor
     * @param nomeMae
     * @param nomePai
     * @param dataNascimento
     * @param sexoEnumOrdinal
     * @return 
     * @throws br.gov.ms.detran.comum.util.exception.AppException 
     */
    public Long gravarPessoa(
            String cpf, String nomeCondutor, String nomeMae, String nomePai, 
            String dataNascimento, Integer sexoEnumOrdinal) throws AppException {

        TipoPessoaEnum tipoPessoa = TipoPessoaEnum.parse(cpf);

        PessoaWrapper2 pessoa = parseTipoPessoa(
                tipoPessoa, cpf, nomeCondutor, nomeMae, 
                nomePai, dataNascimento, sexoEnumOrdinal);

        String pessoaJson = parsePessoa(pessoa);

        DetranWebIntranet intranet = new DetranWebIntranet()
                .resourcePath("/detran/dtnintraweb/pessoa/gravarPessoa")
                .rsParams(DtnIntraPessoaParams.TP_PESSOA, tipoPessoa)
                .rsParams(DtnIntraPessoaParams.WRAPPER, pessoaJson)
                .httpMethod("POST")
                .connect();

        PessoaWrapper2 wrapper = 
                tipoPessoa.equals(TipoPessoaEnum.PESSOA_FISICA) ||
                tipoPessoa.equals(TipoPessoaEnum.AMBAS)
                    ? intranet.getResponseForObject(PessoaFisicaWrapper2.class) 
                    : intranet.getResponseForObject(PessoaJuridicaWrapper2.class);

        if (wrapper == null || wrapper.getId() == null) {
            applicationMessageException("Não foi possível gravar a pessoa {0} - {1}", null, new String[]{cpf, nomeCondutor});
        }

        return Long.parseLong(wrapper.getId().toString());
    }

    /**
     * 
     * @param cpfCnpj
     * @param nome
     * @return 
     */
    PessoaWrapper2 prepararGravacaoPessoaFisica(
            String cpf, String nomeCondutor, String nomeMae, String nomePai, 
            String dataNascimento, Integer sexoEnumOrdinal) {

        PessoaFisicaWrapper2 wrapper = new PessoaFisicaWrapper2();
        wrapper.setFuncionalidadeOrigem(PessoaOrigemEnum.PESSOA_FISICA_CONDUTOR_PROCESSO_ADMINISTRATIVO);

        wrapper.setCpfCnpj(new DocumentoPessoaWrapper(new DocumentoWrapper()));
        wrapper.getCpfCnpj().getEntidade().setNumeroDocumento(cpf);
        wrapper.setNome(new NomeWrapper2(new PessoaNomeGrupoServico(new Nome(nomeCondutor))));

        wrapper.setOrigem(new OrigemWrapper2(new Origem()));
        wrapper.getOrigem().getEntidade().setSexo(SexoEnum.parse(sexoEnumOrdinal));
        wrapper.getOrigem().getEntidade().setMae(nomeMae);
        wrapper.getOrigem().getEntidade().setPai(nomePai);
        wrapper.getOrigem().getEntidade().setDataNascimento(Utils.getDate(dataNascimento, "ddMMyyyy"));

        PessoaFisicaWrapper2 pf = new PessoaFisicaWrapper2();
        pf.setFuncionalidadeOrigem(PessoaOrigemEnum.PESSOA_FISICA_CONDUTOR_PROCESSO_ADMINISTRATIVO);

        pf.setCpfCnpj(new DocumentoPessoaWrapper(new DocumentoWrapper()));
        pf.getCpfCnpj().getEntidade().setNumeroDocumento(cpf);
        pf.setNome(new NomeWrapper2(new PessoaNomeGrupoServico(new Nome(nomeCondutor))));

        pf.setOrigem(new OrigemWrapper2(new Origem()));
        pf.getOrigem().getEntidade().setSexo(SexoEnum.parse(sexoEnumOrdinal));
        pf.setCodigoGrupoServico(CodigoGrupoServico.ADMINISTRATIVO);

        return pf;
    }

    /**
     * 
     * @param cpfCnpj
     * @param nome
     * @return 
     */
    PessoaWrapper2 prepararGravacaoPessoaJuridica(String cpfCnpj, String nome) {

        PessoaJuridicaWrapper2 wrapperJuridica = new PessoaJuridicaWrapper2();
        wrapperJuridica.setFuncionalidadeOrigem(PessoaOrigemEnum.PESSOA_JURIDICA);

        wrapperJuridica.setCpfCnpj(new DocumentoPessoaWrapper(new DocumentoWrapper()));
        wrapperJuridica.getCpfCnpj().getEntidade().setNumeroDocumento(cpfCnpj);
        wrapperJuridica.setNome(new NomePessoaJuridicaWrapper(new Nome(nome)));
        wrapperJuridica.setOrigem(new OrigemWrapper2(new Origem()));
        wrapperJuridica.setCodigoGrupoServico(CodigoGrupoServico.ADMINISTRATIVO);
        wrapperJuridica.setEntidade(new PessoaJuridica());

        return wrapperJuridica;
    }

    PessoaWrapper2 parseTipoPessoa(
            TipoPessoaEnum tipoPessoa, String cpf, String nomeCondutor, 
            String nomeMae, String nomePai, String dataNascimento, 
                                Integer sexoEnumOrdinal) throws AppException {

        PessoaWrapper2 pessoa = null;

        switch(tipoPessoa) {
            case PESSOA_FISICA: {

                pessoa = prepararGravacaoPessoaFisica(
                        cpf, nomeCondutor, nomeMae, nomePai, 
                        dataNascimento, sexoEnumOrdinal
                );

                break;
            }

            case PESSOA_JURIDICA: {
                pessoa = prepararGravacaoPessoaJuridica(cpf, nomeCondutor);
                break;
            }

            case AMBAS: {

                pessoa = prepararGravacaoPessoaFisica(
                        cpf, nomeCondutor, nomeMae, nomePai, 
                        dataNascimento, sexoEnumOrdinal
                );

                break;
            }
            default:{
                throw new ExceptionGeneric("Pessoa inválida!");
            }
        }

        return pessoa;
    }

    /**
     * 
     * @param pessoa
     * @return
     * @throws AppException 
     */
    String parsePessoa(PessoaWrapper2 pessoa) throws AppException {

        try {

            return JsonJacksonUtils.fromObject(pessoa);

        } catch (IOException ex) {
            LOGGER.error("Erro ao converter pessoa para JSON", ex);
            applicationMessageException("Pessoa inválida! Não foi possível montar mensagem para gravar a pessoa.");
        }

        return null;
    }
}