package br.gov.ms.detran.processo.administrativo.enums;

import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.APENSADO.CONFIRMAR_ARQUIVAMENTO_PROCESSO_AGRAVADO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.ARQUIVAMENTO.CONFIRMAR_ARQUIVAMENTO_PROCESSO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.ARQUIVAMENTO.DESBLOQUEAR_WEB_PENA_ANULADA;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018_VARIOS_PROCESSOS;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CANCELAMENTO.CONFIRMAR_CANCELAMENTO_PROCESSO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CANCELAMENTO_RECURSO.CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CANCELAMENTO_RECURSO.CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CANCELAMENTO_RECURSO.CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CASSADO.CONFIRMAR_ARQUIVAMENTO_PROCESSO_CASSADO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_PROTOCOLO_DEVOLUCAO_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.AGUARDAR_REALIZACAO_PROVA_CURSO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.ATUALIZAR_BLOQUEIO_BCA;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.ATUALIZAR_BLOQUEIO_BCA_PJU;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.ATUALIZAR_BLOQUEIO_WEB;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_ARQUIVAMENTO_PENA_CUMPRIDA;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_BLOQUEIO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_DESBLOQUEIO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.CONFIRMAR_PROVA_CURSO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.DESBLOQUEAR_BCA;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.DESBLOQUEAR_WEB;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.INCLUIR_BLOQUEIO_BCA;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.CUMPRIMENTO_PENA.INCLUIR_BLOQUEIO_WEB;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.DESISTENCIA.CONFIRMAR_DESISTENCIA_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.DESISTENCIA.CONFIRMAR_DESISTENCIA_RECURSO_INSTAURACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.DESISTENCIA.DESISTIR_RECURSO_INSTAURACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.EDITAL_NOTIFICACAO.AGUARDAR_EDITAL_NOTIFICACAO_CURSO_EXAME;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.EDITAL_NOTIFICACAO.AGUARDAR_EDITAL_NOTIFICACAO_DESENTRANHAMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.EDITAL_NOTIFICACAO.AGUARDAR_EDITAL_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.EDITAL_NOTIFICACAO.AGUARDAR_EDITAL_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.EDITAL_NOTIFICACAO.AGUARDAR_EDITAL_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.EDITAL_NOTIFICACAO_ACOLHIMENTO.AGUARDAR_EDITAL_NOTIFICACAO_ACOLHIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.EMISSAO_NOTIFICACAO_ARQUIVAMENTO_INDEVIDO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.ESPECIFICO_CORRECAO.ARQUIVAR_PROCESSO_PRESCRITO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.INSTAURACAO.ATUALIZAR_INFRACOES_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.INSTAURACAO.CONFIRMAR_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.INSTAURACAO.CONFIRMAR_INSTAURACAO_ENVIO_89;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.INSTAURACAO.GERAR_JSON;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.INSTAURACAO.INSTAURAR_PROCESSO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.AGUARDAR_AR_NOTIFICACAO_ACOLHIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.AGUARDAR_GERACAO_NOTIFICACAO_ACOLHIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.CONFIRMAR_NOTIFICACAO_ACOLHIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.CONFIRMAR_RETORNO_AR_NOTIFICACAO_ACOLHIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_ACOLHIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_ACOLHIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.GERAR_PDF_NOTIFICACAO_ACOLHIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ACOLHIMENTO.TRANSFERIR_SGI_NOTIFICACAO_ACOLHIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.AGUARDAR_AR_NOTIFICACAO_CURSO_EXAME;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.CONFIRMAR_NOTIFICACAO_CURSO_EXAME;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.CONFIRMAR_RETORNO_AR_NOTIFICACAO_CURSO_EXAME;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_CURSO_EXAME;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.GERAR_NOTIFICACAO_CURSO_EXAME;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_CURSO_EXAME.GERAR_PDF_NOTIFICACAO_CURSO_EXAME;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.AGUARDAR_AR_NOTIFICACAO_DESENTRANHAMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.CALCULAR_REGRA_CONCATENACAO_BLOQUEIO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.CONFIRMAR_NOTIFICACAO_DESENTRANHAMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.CONFIRMAR_RETORNO_AR_NOTIFICACAO_DESENTRANHAMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_DESENTRANHAMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_DESENTRANHAMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.GERAR_NOTIFICACAO_DESENTRANHAMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.GERAR_PDF_NOTIFICACAO_DESENTRANHAMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_DESENTRANHAMENTO.TRANSFERIR_SGI_NOTIFICACAO_DESENTRANHAMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_AR_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.CONFIRMAR_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.CONFIRMAR_RETORNO_AR_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.GERAR_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.GERAR_PDF_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_ENTREGA_CNH.TRANSFERIR_SGI_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.AGUARDAR_AR_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.AGUARDAR_GERACAO_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.CONFIRMAR_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.CONFIRMAR_RETORNO_AR_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.GERAR_PDF_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_INSTAURACAO.TRANSFERIR_SGI_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_JARI.AGUARDAR_AR_NOTIFICACAO_JARI;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_JARI.AGUARDAR_EDITAL_NOTIFICACAO_JARI;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_JARI.AGUARDAR_GERACAO_NOTIFICACAO_JARI;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_JARI.AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_JARI;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_JARI.CONFIRMAR_NOTIFICACAO_JARI;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_JARI.CONFIRMAR_RETORNO_AR_NOTIFICACAO_JARI;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_JARI.CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_JARI;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_JARI.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_JARI;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_JARI.GERAR_PDF_NOTIFICACAO_JARI;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_NAO_CONHECIMENTO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_NAO_CONHECIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_NAO_CONHECIMENTO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_NAO_CONHECIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_NAO_CONHECIMENTO.GERAR_NOTIFICACAO_NAO_CONHECIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_NAO_CONHECIMENTO.GERAR_PDF_NOTIFICACAO_NAO_CONHECIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_NAO_CONHECIMENTO.TRANSFERIR_SGI_NOTIFICACAO_NAO_CONHECIMENTO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.AGUARDAR_AR_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.AGUARDAR_GERACAO_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.CONFIRMAR_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.CONFIRMAR_RETORNO_AR_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.GERAR_PDF_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.NOTIFICACAO_PENALIZACAO.TRANSFERIR_SGI_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.PROCESSO_JURIDICO.CONFIRMAR_DESBLOQUEIO_CANDIDATO_CIDADAO_PJU;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.PROCESSO_JURIDICO.CONFIRMAR_DESBLOQUEIO_CARTORIO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.PROCESSO_JURIDICO.CONFIRMAR_ENTREGA_CNH_CARTORIO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.PROCESSO_JURIDICO.CUMPRIR_PENALIDADE;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.PROCESSO_JURIDICO.GERAR_JSON_PJU;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.PROCESSO_JURIDICO.GERAR_NOTIFICACAO_DESENTRANHAMENTO_PJU;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.PROCESSO_JURIDICO.GERAR_PENALIDADE;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.PROCESSO_JURIDICO.INCLUIR_BLOQUEIO_BCA_PJU;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_ENTREGA_CNH.INCLUIR_RECURSO_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_ENTREGA_CNH.INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_ENTREGA_CNH;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_INSTAURACAO.INCLUIR_RECURSO_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_INSTAURACAO.INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_INSTAURACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_PENALIZACAO.INCLUIR_RECURSO_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.RECURSO_NOTIFICACAO_PENALIZACAO.INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_PENALIZACAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_RETIRADA_DA_SUSPENSAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.SUSPENSO.CONFIRMAR_SUSPENSAO;
import static br.gov.ms.detran.processo.administrativo.constantes.PAAndamentoProcessoConstante.SUSPENSO.PROCESSO_SUSPENSO;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento114;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento115;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento116;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento117;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento118;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento119;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento120;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento121;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento122;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento123;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento124;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento125;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento126;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento127;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento128;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento129;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento130;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento131;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento132;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento133;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento134;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento135;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento136;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento137;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento138;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento139;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento140;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento141;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento142;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento143;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento144;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento145;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento146;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento147;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento148;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento149;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento15;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento150;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento151;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento152;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento153;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento154;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento155;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento156;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento157;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento158;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento159;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento161;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento162;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento163;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento166;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento167;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento168;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento169;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento170;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento171;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento172;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento173;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento175;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento183;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento184;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento185;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento186;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento187;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento188;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento189;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento190;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento193;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento194;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento195;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento196;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento198;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento199;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento2;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento202;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento203;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento204;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento205;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento206;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento207;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento208;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento21;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento210;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento211;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento212;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento213;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento214;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento215;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento216;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento217;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento22;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento23;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento231;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento232;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento233;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento234;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento235;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento236;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento237;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento238;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento240;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento241;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento243;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento244;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento26;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento31;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento32;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento33;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento36;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento39;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento40;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento41;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento42;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento43;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento44;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento45;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento46;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento48;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento50;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento51;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento53;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento54;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento60;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento64;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento70;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento72;
import br.gov.ms.detran.processo.administrativo.core.andamento.PAAndamento73;
import br.gov.ms.detran.processo.administrativo.ejb.IExecucaoAndamento;
import br.gov.ms.detran.processo.administrativo.entidade.PAAndamentoProcessoEspecifico;
import br.gov.ms.detran.processo.administrativo.wrapper.RecursoWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EditalWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.EntregaCnhWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.MovimentacaoPaWSWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.NotificaProcessoAdministrativoWrapper;
import br.gov.ms.detran.processo.administrativo.wrapper.ws.RecursoCanceladoWrapper;


/**
 * @author Lillydi
 */
public enum PAAndamentoEnum {
    
    ANDAMENTO_2(
        INCLUIR_BLOQUEIO_BCA_PJU, new PAAndamento2()
    ),
    ANDAMENTO_15(
        AGUARDAR_GERACAO_NOTIFICACAO_INSTAURACAO, new PAAndamento15(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_21(
        AGUARDAR_AR_NOTIFICACAO_INSTAURACAO, new PAAndamento21()
    ),
    ANDAMENTO_22(
        AGUARDAR_EDITAL_NOTIFICACAO_INSTAURACAO, new PAAndamento22(), new EditalWrapper()
    ),
    ANDAMENTO_23(
        INCLUIR_RECURSO_NOTIFICACAO_INSTAURACAO, new PAAndamento23(), new RecursoWSWrapper()
    ),
    ANDAMENTO_26(
        AGUARDAR_GERACAO_NOTIFICACAO_PENALIZACAO, new PAAndamento26(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_31(
        AGUARDAR_AR_NOTIFICACAO_PENALIZACAO, new PAAndamento31()
    ),
    ANDAMENTO_32(
        AGUARDAR_EDITAL_NOTIFICACAO_PENALIZACAO, new PAAndamento32(), new EditalWrapper()
    ),
    ANDAMENTO_33(
        INCLUIR_RECURSO_NOTIFICACAO_PENALIZACAO, new PAAndamento33(), new RecursoWSWrapper()
    ),
    ANDAMENTO_36(
        INCLUIR_RECURSO_NOTIFICACAO_ENTREGA_CNH, new PAAndamento36(), new RecursoWSWrapper()
    ),
    ANDAMENTO_39(
        AGUARDAR_GERACAO_NOTIFICACAO_ENTREGA_CNH, new PAAndamento39(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_40(
        GERAR_NOTIFICACAO_ENTREGA_CNH, new PAAndamento40(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_41(
        AGUARDAR_AR_NOTIFICACAO_ENTREGA_CNH, new PAAndamento41()
    ),
    ANDAMENTO_42(
        AGUARDAR_EDITAL_NOTIFICACAO_ENTREGA_CNH, new PAAndamento42(), new EditalWrapper()
    ),
    ANDAMENTO_43(
        INCLUIR_BLOQUEIO_WEB, new PAAndamento43()
    ),
    ANDAMENTO_44(
        INCLUIR_BLOQUEIO_BCA, new PAAndamento44()
    ),
    ANDAMENTO_45(
        ATUALIZAR_BLOQUEIO_BCA, new PAAndamento45()
    ),
    ANDAMENTO_46(
        AGUARDAR_GERACAO_NOTIFICACAO_ACOLHIMENTO, new PAAndamento46(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_48(
        AGUARDAR_ENTREGA_CNH, new PAAndamento48()
    ),
    ANDAMENTO_50(
        AGUARDAR_AR_NOTIFICACAO_ACOLHIMENTO, new PAAndamento50()
    ),
    ANDAMENTO_51(
        AGUARDAR_EDITAL_NOTIFICACAO_ACOLHIMENTO, new PAAndamento51(), new EditalWrapper()
    ),
    ANDAMENTO_53(
       AGUARDAR_AR_NOTIFICACAO_DESENTRANHAMENTO, new PAAndamento53()
    ),
    ANDAMENTO_54(
       AGUARDAR_EDITAL_NOTIFICACAO_DESENTRANHAMENTO, new PAAndamento54(), new EditalWrapper()
    ),
    ANDAMENTO_60(
       AGUARDAR_REALIZACAO_PROVA_CURSO, new PAAndamento60()
    ),
    ANDAMENTO_70(
       DESBLOQUEAR_BCA, new PAAndamento70()
    ),
    ANDAMENTO_64(
        AGUARDAR_PROTOCOLO_DEVOLUCAO_CNH, new PAAndamento64()
    ),
    ANDAMENTO_72(
        CONFIRMAR_ARQUIVAMENTO_PENA_CUMPRIDA, new PAAndamento72(), new MovimentacaoPaWSWrapper()
    ),
    ANDAMENTO_73(
        CONFIRMAR_ARQUIVAMENTO_RECURSO_PROVIDO, new PAAndamento73(), new MovimentacaoPaWSWrapper()
    ),
    ANDAMENTO_114(
        INSTAURAR_PROCESSO, new PAAndamento114()
    ),
    ANDAMENTO_115(
        CONFIRMAR_INSTAURACAO_ENVIO_89, new PAAndamento115()
    ),
    ANDAMENTO_116(
        GERAR_JSON, new PAAndamento116()
    ),
    ANDAMENTO_117(
        ATUALIZAR_INFRACOES_INSTAURACAO, new PAAndamento117()
    ),
    ANDAMENTO_118(
        CONFIRMAR_INSTAURACAO, new PAAndamento118()
    ),
    ANDAMENTO_119(
        GERAR_PDF_NOTIFICACAO_INSTAURACAO, new PAAndamento119()
    ),
    ANDAMENTO_120(
        GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_INSTAURACAO, new PAAndamento120()
    ),
    ANDAMENTO_121(
        ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_INSTAURACAO, new PAAndamento121()
    ),
    ANDAMENTO_122(
        TRANSFERIR_SGI_NOTIFICACAO_INSTAURACAO, new PAAndamento122()
    ),
    ANDAMENTO_123(
        CONFIRMAR_NOTIFICACAO_INSTAURACAO, new PAAndamento123(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_124(
        CONFIRMAR_RETORNO_AR_NOTIFICACAO_INSTAURACAO, new PAAndamento124(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_125(
        GERAR_PDF_NOTIFICACAO_PENALIZACAO, new PAAndamento125()
    ),
    ANDAMENTO_126(
        GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_PENALIZACAO, new PAAndamento126()
    ),
    ANDAMENTO_127(
        ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_PENALIZACAO, new PAAndamento127()
    ),
    ANDAMENTO_128(
        TRANSFERIR_SGI_NOTIFICACAO_PENALIZACAO, new PAAndamento128()
    ),
    ANDAMENTO_129(
        CONFIRMAR_NOTIFICACAO_PENALIZACAO, new PAAndamento129(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_130(
        CONFIRMAR_RETORNO_AR_NOTIFICACAO_PENALIZACAO, new PAAndamento130(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_131(
        GERAR_PDF_NOTIFICACAO_ENTREGA_CNH, new PAAndamento131()
    ),
    ANDAMENTO_132(
        GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_ENTREGA_CNH, new PAAndamento132()
    ),
    ANDAMENTO_133(
        ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_ENTREGA_CNH, new PAAndamento133()
    ),
    ANDAMENTO_134(
        TRANSFERIR_SGI_NOTIFICACAO_ENTREGA_CNH, new PAAndamento134()
    ),
    ANDAMENTO_135(
        CONFIRMAR_NOTIFICACAO_ENTREGA_CNH, new PAAndamento135(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_136(
        CONFIRMAR_RETORNO_AR_NOTIFICACAO_ENTREGA_CNH, new PAAndamento136(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_137(
        CONFIRMAR_BLOQUEIO, new PAAndamento137()
    ),
    ANDAMENTO_138(
        CONFIRMAR_ENTREGA_CNH, new PAAndamento138(), new EntregaCnhWrapper()
    ),
    ANDAMENTO_139(
        CONFIRMAR_PROVA_CURSO, new PAAndamento139()
    ),
    ANDAMENTO_140(
        CONFIRMAR_DESBLOQUEIO, new PAAndamento140(), new EntregaCnhWrapper()
    ),
    ANDAMENTO_141(
        INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_INSTAURACAO, new PAAndamento141(), new RecursoWSWrapper()
    ),
    ANDAMENTO_142(
        INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_PENALIZACAO, new PAAndamento142(), new RecursoWSWrapper()
    ),
    ANDAMENTO_143(
        INCLUIR_RESULTADO_RECURSO_NOTIFICACAO_ENTREGA_CNH, new PAAndamento143(), new RecursoWSWrapper()
    ),
    ANDAMENTO_144(
        CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_INSTAURACAO, new PAAndamento144(), new RecursoCanceladoWrapper()
    ),
    ANDAMENTO_145(
        CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_PENALIZACAO, new PAAndamento145(), new RecursoCanceladoWrapper()
    ),
    ANDAMENTO_146(
        CONFIRMAR_CANCELAMENTO_RECURSO_NOTIFICACAO_ENTREGA_CNH, new PAAndamento146(), new RecursoCanceladoWrapper()
    ),
    ANDAMENTO_147(
        CONFIRMAR_DESISTENCIA_ENTREGA_CNH, new PAAndamento147()
    ),
    ANDAMENTO_148(
        GERAR_PDF_NOTIFICACAO_ACOLHIMENTO, new PAAndamento148()
    ),
    ANDAMENTO_149(
        GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_ACOLHIMENTO, new PAAndamento149()
    ),
    ANDAMENTO_150(
        ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_ACOLHIMENTO, new PAAndamento150()
    ),
    ANDAMENTO_151(
        TRANSFERIR_SGI_NOTIFICACAO_ACOLHIMENTO, new PAAndamento151()
    ),
    ANDAMENTO_152(
        CONFIRMAR_NOTIFICACAO_ACOLHIMENTO, new PAAndamento152(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_153(
        CONFIRMAR_RETORNO_AR_NOTIFICACAO_ACOLHIMENTO, new PAAndamento153(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_154(
        GERAR_NOTIFICACAO_NAO_CONHECIMENTO, new PAAndamento154()
    ),
    ANDAMENTO_155(
        GERAR_PDF_NOTIFICACAO_NAO_CONHECIMENTO, new PAAndamento155()
    ),
    ANDAMENTO_156(
        GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_NAO_CONHECIMENTO, new PAAndamento156()
    ),
    ANDAMENTO_157(
        ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_NAO_CONHECIMENTO, new PAAndamento157()
    ),
    ANDAMENTO_158(
        TRANSFERIR_SGI_NOTIFICACAO_NAO_CONHECIMENTO, new PAAndamento158()
    ),
    ANDAMENTO_159(
        CONFIRMAR_CANCELAMENTO_PROCESSO, new PAAndamento159(), new MovimentacaoPaWSWrapper()
    ),
    ANDAMENTO_161(
        PROCESSO_SUSPENSO, new PAAndamento161()
    ),
    ANDAMENTO_162(
        CONFIRMAR_RETIRADA_DA_SUSPENSAO, new PAAndamento162(), new MovimentacaoPaWSWrapper()
    ),
    ANDAMENTO_163(
        CONFIRMAR_ARQUIVAMENTO_PROCESSO, new PAAndamento163(), new MovimentacaoPaWSWrapper()
    ),
    ANDAMENTO_166(
        GERAR_NOTIFICACAO_DESENTRANHAMENTO, new PAAndamento166(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_167(
        GERAR_PDF_NOTIFICACAO_DESENTRANHAMENTO, new PAAndamento167()
    ),
    ANDAMENTO_168(
        GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_DESENTRANHAMENTO, new PAAndamento168()
    ),
    ANDAMENTO_169(
        ENVIAR_ARQUIVO_CORREIOS_NOTIFICACAO_DESENTRANHAMENTO, new PAAndamento169()
    ),
    ANDAMENTO_170(
        TRANSFERIR_SGI_NOTIFICACAO_DESENTRANHAMENTO, new PAAndamento170()
    ),
    ANDAMENTO_171(
        CONFIRMAR_NOTIFICACAO_DESENTRANHAMENTO, new PAAndamento171(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_172(
        CONFIRMAR_RETORNO_AR_NOTIFICACAO_DESENTRANHAMENTO, new PAAndamento172(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_173(
        CONFIRMAR_ARQUIVAMENTO_PROCESSO_AGRAVADO, new PAAndamento173(), new MovimentacaoPaWSWrapper()
    ),
    ANDAMENTO_175(
        CONFIRMAR_ARQUIVAMENTO_PROCESSO_CASSADO, new PAAndamento175(), new MovimentacaoPaWSWrapper()
    ),
    ANDAMENTO_183(
        ATUALIZAR_BLOQUEIO_WEB, new PAAndamento183()
    ),
    ANDAMENTO_184(
        DESBLOQUEAR_WEB, new PAAndamento184()
    ),
    ANDAMENTO_185(
        DESISTIR_RECURSO_INSTAURACAO_PENALIZACAO, new PAAndamento185()
    ),
    ANDAMENTO_186(
        CONFIRMAR_DESISTENCIA_RECURSO_INSTAURACAO_PENALIZACAO, new PAAndamento186()
    ),
    ANDAMENTO_187(
        ATUALIZAR_BLOQUEIO_BCA_PJU, new PAAndamento187()
    ),
    ANDAMENTO_188(
        GERAR_NOTIFICACAO_CURSO_EXAME, new PAAndamento188(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_189(
        GERAR_PDF_NOTIFICACAO_CURSO_EXAME, new PAAndamento189()
    ),
    ANDAMENTO_190(
        GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_CURSO_EXAME, new PAAndamento190()
    ),
    ANDAMENTO_193(
        CONFIRMAR_NOTIFICACAO_CURSO_EXAME, new PAAndamento193(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_194(
        AGUARDAR_AR_NOTIFICACAO_CURSO_EXAME, new PAAndamento194()
    ),
    ANDAMENTO_195(
        CONFIRMAR_RETORNO_AR_NOTIFICACAO_CURSO_EXAME, new PAAndamento195(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_196(
        AGUARDAR_EDITAL_NOTIFICACAO_CURSO_EXAME, new PAAndamento196(), new EditalWrapper()
    ),
    ANDAMENTO_198(
        GERAR_PDF_NOTIFICACAO_JARI, new PAAndamento198()
    ),
    ANDAMENTO_199(
        GERAR_ARQUIVO_CORREIOS_NOTIFICACAO_JARI, new PAAndamento199()
    ),
    ANDAMENTO_202(
        CONFIRMAR_NOTIFICACAO_JARI, new PAAndamento202(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_203(
        AGUARDAR_AR_NOTIFICACAO_JARI, new PAAndamento203()
    ),
    ANDAMENTO_204(
        CONFIRMAR_RETORNO_AR_NOTIFICACAO_JARI, new PAAndamento204(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_205(
        AGUARDAR_EDITAL_NOTIFICACAO_JARI, new PAAndamento205(), new EditalWrapper()
    ),
    ANDAMENTO_206(
        AGUARDAR_GERACAO_NOTIFICACAO_JARI, new PAAndamento206(), new NotificaProcessoAdministrativoWrapper()
    ),
    ANDAMENTO_207(
        CONFIRMAR_SUSPENSAO, new PAAndamento207(), new MovimentacaoPaWSWrapper()
    ),
    ANDAMENTO_208(
        GERAR_JSON_PJU, new PAAndamento208()
    ),
    ANDAMENTO_210(
        DESBLOQUEAR_WEB_PENA_ANULADA, new PAAndamento210()
    ),
    ANDAMENTO_211(
            CUMPRIR_PENALIDADE, new PAAndamento211()
    ),
    ANDAMENTO_212(
        GERAR_PENALIDADE, new PAAndamento212()
    ),
    ANDAMENTO_213(
        CONFIRMAR_ENTREGA_CNH_CARTORIO, new PAAndamento213(), new EntregaCnhWrapper()
    ),
    ANDAMENTO_214(
        CONFIRMAR_DESBLOQUEIO_CARTORIO, new PAAndamento214(), new EntregaCnhWrapper()
    ),
    ANDAMENTO_215(
        GERAR_NOTIFICACAO_DESENTRANHAMENTO_PJU, new PAAndamento215()
    ),
    ANDAMENTO_216(
        CONFIRMAR_DESBLOQUEIO_CANDIDATO_CIDADAO_PJU, new PAAndamento216()
    ),
    ANDAMENTO_217(
        ARQUIVAR_PROCESSO_PRESCRITO, new PAAndamento217(), new PAAndamentoProcessoEspecifico()
    ),
    ANDAMENTO_231(
            AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_INSTAURACAO, new PAAndamento231()
    ),
    ANDAMENTO_232(
            CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_INSTAURACAO, new PAAndamento232()
    ),
    ANDAMENTO_233(
            AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_PENALIZACAO, new PAAndamento233()
    ),
    ANDAMENTO_234(
            CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_PENALIZACAO, new PAAndamento234()
    ),
    ANDAMENTO_235(
            AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_ENTREGA_CNH, new PAAndamento235()
    ),
    ANDAMENTO_236(
            CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_ENTREGA_CNH, new PAAndamento236()
    ),
    ANDAMENTO_237(
            AGUARDAR_TERMINO_PRAZO_NOTIFICACAO_JARI, new PAAndamento237()
    ),
    ANDAMENTO_238(
            CONFIRMAR_TERMINO_PRAZO_NOTIFICACAO_JARI, new PAAndamento238()
    ),
    ANDAMENTO_240(
            ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018, new PAAndamento240()
    ),
    ANDAMENTO_241(
            ATUALIZAR_BLOQUEIO_BCA_RESOLUCAO_723_2018_VARIOS_PROCESSOS, new PAAndamento241()
    ),
    ANDAMENTO_243(
            EMISSAO_NOTIFICACAO_ARQUIVAMENTO_INDEVIDO, new PAAndamento243()
    ),
    ANDAMENTO_244(
            CALCULAR_REGRA_CONCATENACAO_BLOQUEIO, new PAAndamento244()
    )
    ;
    
    private final Integer codigo;
    private final IExecucaoAndamento execucao;
    private final Object objetoEntrada;
    
    private PAAndamentoEnum(Integer codigo, IExecucaoAndamento iExecucaoAndamento, Object ...objetoEntrada) {
        this.codigo         = codigo;
        this.execucao       = iExecucaoAndamento;
        this.objetoEntrada  = objetoEntrada;
    }
    
    public Integer getCodigo() {
        return codigo;
    }

    public IExecucaoAndamento getAndamento() {
        return execucao;
    }

    public Object getObjetoEntrada() {
        return objetoEntrada;
    }
}
