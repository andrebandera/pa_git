/**
 *
 * @author Carlos Eduardo
 * Data: 18/07/2019
 */

//Create the module
angular.module('tipocorpoandamento', ['ngRoute'])

//Variaveis que nao vareiam rsrs
        .constant("tipocorpoandamentoConfig", {
            name: 'tipocorpoandamento',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/patipocorpoandamentos/search',
                "gravar": 'detran-processo-administrativo/resource/patipocorpoandamentos/save',
                "desativar": 'detran-processo-administrativo/resource/patipocorpoandamentos/',
                "reativar": 'detran-processo-administrativo/resource/patipocorpoandamentos/reativar',
                "novo": 'detran-processo-administrativo/resource/patipocorpoandamentos/new'
            },
            model: [
                {field: "entidade.id", hidden: "hide"},
                {field: "entidade.versaoRegistro", hidden: "hide"},
                {field: "entidade.origemProcesso.descricao", displayName: "tipocorpoandamento.label.descricao"},
                {field: "tipoCorpoTexto.descricao", displayName: "tipocorpoandamento.label.tipoCorpoTexto", isJson: true},
                {field: "entidade.tipoNotificacaoProcesso", displayName: "tipocorpoandamento.label.tipoNotificacaoProcesso"},
                {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
                {field: "actions", actions: [
                        {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
                        {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
                        {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"}
                    ]}
            ],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/tipocorpoandamento',
            i18nextResources: {
                "tipocorpoandamento": {"titulo": "Tipo Corpo Andamento",
                    "label": {
                        "tipocorpoandamento": "Tipo Corpo Andamento",
                        "descricao": "Descrição",
                        "origemProcesso": "Origem Processo",
                        "regra": "Regra",
                        "fluxoProcessoEdital": "Fluxo Processo Edital",
                        "fluxoProcessoRecurso": "Fluxo Processo Recurso",
                        "fluxoProcessoRecursoCancelado": "Fluxo Processo Recurso Cancelado",
                        "tipoCorpoTexto": "Tipo Corpo Texto",
                        "tipoNotificacaoProcesso": "Tipo Notificação Processo",
                        "motivo": "Origem Instauração",
                        "codigo": "Código"
                    }
                }
            },
            hasMestre: true
        })

//Este é o controller Principal.
        .controller('tipocorpoandamentoCtrl', ['$scope', '$injector', 'tipocorpoandamentoConfig', 'srvDetranAbstractResourceRest',
            '$rootScope', 'srvTable', "detranModal", 'utils', '$location',
            function ($scope, $injector, config, srvRest, $rootScope, srvTable, dtnModal, utils, $location) {
                //aviso que qndo sou chamado, ainda nao tenho conteudo
                // vou ter conteudo somente qndo clicarem na minha aba
                $scope.hasData = false;


                //Somente qndo clica na tab é que devo ler os conteúdos
                $scope.$on('tabClicked', function (event, tab) {
                    if (tab !== 'tipocorpoandamento')
                        return;

                    // se eu nao tenho conteudo, leio o $scope.start para criar os conteudos
                    if (!$scope.hasData) {
                        $scope.start();
                        $scope.hasData = true; // agora eu tenho conteudo, nao preciso ler mais o $scope.start
                    } else {
                        $scope.clear();
                        var mestreID = angular.copy($rootScope.mestre.fluxoFase);
                        mestreID.ativo = "ATIVO";
                        $scope.$broadcast('updateData', mestreID, true, config.name); // faco um reload dos conteudos
                    }
                });

                //Leio o conteúdo
                $scope.start = function () {
                    var mestreID = angular.copy($rootScope.mestre.fluxoFase);
                    if ($rootScope.mestre.fluxoFase === undefined || $rootScope.mestre.fluxoFase === '') {
                        return;
                    }
                    $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
                    $injector.invoke(mestreDetalheBasicCtrl, this, {$scope: $scope, config: config});
                    mestreID.ativo = mestreID.ativo || "ATIVO";
                    $scope.urlPath = $scope.$parent.urlPath;
                    $scope.dataModal = {};


                    //DADOS DA DIRETIVA DETRAN-TABLE Q ESTA NO INDEX.HTML
                    $scope.config = config;
                    $scope.mestreID = angular.copy(mestreID);
                    
                    //Defino os botoes que uso na minha table
                    var actionButtons = ($scope.page === 'view') ? ['ver'] : ['ver', 'desativar', 'reativar'];
                    //funcionalidade interrompida apenas botão ver       
                    $scope.actionButtons = srvTable.actionButtons(actionButtons);

                    //getAction of buttons of table
                    // o $emit esta na detranTable
                    $scope.$on('getAction' + $scope.config, function (event, data) {
                        if (data.myName !== 'tipocorpoandamento')
                            return;
                        $scope.action(data.action, data.data);
                    });

                    //// Verifico se os dados do $scope.data foram carregados corretamente
                    //// o $emit está no mestreDetalheBasicCtrl na funcao $scope.action
                    $scope.$on('dataIsLoaded', function () {
                        $scope.dataModal.origemProcesso = $scope.data.entidade.origemProcesso.descricao;
                        $scope.dataModal.fluxoProcessoEdital = $scope.data.entidade.fluxoProcessoEdital.descricao;
                        $scope.dataModal.fluxoProcessoRecurso = $scope.data.entidade.fluxoProcessoRecurso.descricao;
                        $scope.dataModal.fluxoProcessoRecursoCancelado = $scope.data.entidade.fluxoProcessoRecursoCancelado.descricao;
                        $scope.dataModal.tipoCorpoTexto= $scope.data.tipoCorpoTexto.descricao;

                    });

                    ////////////////////////////////////////////////////////////////////
                    //// Qndo dou duplo clique ou enter no campo 
                    $scope.origemInstauracaoModal = function () {
                        var configModal = angular.copy(config);
                        configModal.itemsPerPage = 10;
                        configModal.limitSizePagination = true;
                        var modalConfiguration = {
                            name: 'OrigemInstauracaoModal',
                            msgItensVazio: "Já existe registro",
                            resourcePath: 'detran-processo-administrativo/resource/apoioorigeminstauracaos/search',
                            params: {
                                descricao: $scope.dataModal.origemProcesso,
                                ativo: "ATIVO"
                            },
                            inputModel: 'dataModal.origemProcesso',
                            inputHidden: 'data.entidade.origemProcesso',

                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'entidade.id', hidden: "hide"},
                                    {field: 'entidade.versaoRegistro', hidden: "hide"},
                                    {field: 'entidade.regra', displayName: "tipocorpoandamento.label.regra"},
                                    {field: 'entidade.descricao', displayName: "tipocorpoandamento.label.descricao"}
                                ]
                            },
                            fieldDisplayName: 'Origem Processo',
                            config: configModal

                        };
                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                    };

                    $scope.fluxoProcessoEditalModal = function () {
                        var configModal = angular.copy(config);
                        configModal.itemsPerPage = 10;
                        configModal.limitSizePagination = true;
                        var modalConfiguration = {
                            name: 'fluxoProcessoEditalModal',
                            msgItensVazio: "Já existe registro",
                            resourcePath: 'detran-processo-administrativo/resource/pafluxoprocessos/search',
                            params: {
                                descricao: $scope.dataModal.fluxoProcessoEdital,
                                ativo: "ATIVO"
                            },
                            inputModel: 'dataModal.fluxoProcessoEdital',
                            inputHidden: 'data.entidade.fluxoProcessoEdital',

                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'entidade.id', hidden: "hide"},
                                    {field: 'entidade.versaoRegistro', hidden: "hide"},
                                    {field: 'entidade.codigo', displayName: "tipocorpoandamento.label.codigo"},
                                    {field: 'entidade.descricao', displayName: "tipocorpoandamento.label.descricao"}
                                ]
                            },
                            fieldDisplayName: 'Fluxo Processo Edital',
                            config: configModal

                        };
                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                    };

                    $scope.fluxoProcessoRecursoModal = function () {
                        var configModal = angular.copy(config);
                        configModal.itemsPerPage = 10;
                        configModal.limitSizePagination = true;
                        var modalConfiguration = {
                            name: 'fluxoProcessoRecursoModal',
                            msgItensVazio: "Já existe registro",
                            resourcePath: 'detran-processo-administrativo/resource/pafluxoprocessos/search',
                            params: {
                                descricao: $scope.dataModal.fluxoProcessoRecurso,
                                ativo: "ATIVO"
                            },
                            inputModel: 'dataModal.fluxoProcessoRecurso',
                            inputHidden: 'data.entidade.fluxoProcessoRecurso',

                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'entidade.id', hidden: "hide"},
                                    {field: 'entidade.versaoRegistro', hidden: "hide"},
                                    {field: 'entidade.codigo', displayName: "tipocorpoandamento.label.codigo"},
                                    {field: 'entidade.descricao', displayName: "tipocorpoandamento.label.descricao"}
                                ]
                            },
                            fieldDisplayName: 'Fluxo Processo Recurso',
                            config: configModal

                        };
                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                    };

                    $scope.fluxoProcessoRecursoCanceladoModal = function () {
                        var configModal = angular.copy(config);
                        configModal.itemsPerPage = 10;
                        configModal.limitSizePagination = true;
                        var modalConfiguration = {
                            name: 'fluxoProcessoRecursoCanceladoModal',
                            msgItensVazio: "Já existe registro",
                            resourcePath: 'detran-processo-administrativo/resource/pafluxoprocessos/search',
                            params: {
                                descricao: $scope.dataModal.fluxoProcessoRecursoCancelado,
                                ativo: "ATIVO"
                            },
                            inputModel: 'dataModal.fluxoProcessoRecursoCancelado',
                            inputHidden: 'data.entidade.fluxoProcessoRecursoCancelado',

                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'entidade.id', hidden: "hide"},
                                    {field: 'entidade.versaoRegistro', hidden: "hide"},
                                    {field: 'entidade.codigo', displayName: "tipocorpoandamento.label.codigo"},
                                    {field: 'entidade.descricao', displayName: "tipocorpoandamento.label.descricao"}
                                ]
                            },
                            fieldDisplayName: 'Fluxo Processo Recurso Cancelado',
                            config: configModal

                        };
                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                    };

                    $scope.tipoCorpoTexto = "";

                    $scope.tipoCorpoTextoModal = function () {
                        var configModal = angular.copy(config);
                        configModal.itemsPerPage = 10;
                        configModal.limitSizePagination = true;
                        var modalConfiguration = {
                            name: "tipoCorpoTextoModal",
                            resourcePath: 'tipocorpotextos/search',
                            params: {
                                descricao: $scope.dataModal.tipoCorpoTexto
                            },
                            inputModel: 'dataModal.tipoCorpoTexto',
                            inputHidden: 'data.tipoCorpoTexto',
                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'entidade.id', hidden: "hide"},
                                    {field: 'entidade.descricao', displayName: "Descrição"}
                                ],
                                "title": 'Tipo Corpo Texto'
                            },
                            config: configModal
                        };

                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                    };
                    $scope.$on('selectedModalItemOrigemInstauracaoModal', function (event, data) {
                        if (angular.isUndefined(data))
                            return;
                        if (angular.isUndefined(data.entidade)) {
                            $scope.data.entidade.origemProcesso = {
                                id: data.id,
                                versaoRegistro: data.versaoRegistro
                            };
                            $scope.dataModal.origemProcesso = data.descricao;
                        } else {
                            $scope.data.entidade.origemProcesso = {
                                id: data.entidade.id,
                                versaoRegistro: data.entidade.versaoRegistro
                            };
                            $scope.dataModal.origemProcesso = data.entidade.origemProcesso.descricao;
                        }
                    });

                    $scope.$on('selectedModalItemfluxoProcessoEditalModal', function (event, data) {
                        if (angular.isUndefined(data))
                            return;
                        if (angular.isUndefined(data.entidade)) {
                            $scope.data.entidade.fluxoProcessoEdital = {
                                id: data.id,
                                versaoRegistro: data.versaoRegistro
                            };
                            $scope.dataModal.fluxoProcessoEdital = data.descricao;
                        } else {
                            $scope.data.entidade.fluxoProcessoEdital = {
                                id: data.entidade.id,
                                versaoRegistro: data.entidade.versaoRegistro
                            };
                            $scope.dataModal.fluxoProcessoEdital = data.entidade.fluxoProcessoEdital.descricao;
                        }
                    });

                    $scope.$on('selectedModalItemfluxoProcessoRecursoModal', function (event, data) {
                        if (angular.isUndefined(data))
                            return;
                        if (angular.isUndefined(data.entidade)) {
                            $scope.data.entidade.fluxoProcessoRecurso = {
                                id: data.id,
                                versaoRegistro: data.versaoRegistro
                            };
                            $scope.dataModal.fluxoProcessoRecurso = data.descricao;
                        } else {
                            $scope.data.entidade.fluxoProcessoRecurso = {
                                id: data.entidade.id,
                                versaoRegistro: data.entidade.versaoRegistro
                            };
                            $scope.dataModal.fluxoProcessoRecurso = data.entidade.fluxoProcessoRecurso.descricao;
                        }
                    });

                    $scope.$on('selectedModalItemfluxoProcessoRecursoCanceladoModal', function (event, data) {
                        if (angular.isUndefined(data))
                            return;
                        if (angular.isUndefined(data.entidade)) {
                            $scope.data.entidade.fluxoProcessoRecursoCancelado = {
                                id: data.id,
                                versaoRegistro: data.versaoRegistro
                            };
                            $scope.dataModal.fluxoProcessoRecursoCancelado = data.descricao;
                        } else {
                            $scope.data.entidade.fluxoProcessoRecursoCancelado = {
                                id: data.entidade.id,
                                versaoRegistro: data.entidade.versaoRegistro
                            };
                            $scope.dataModal.fluxoProcessoRecursoCancelado = data.entidade.fluxoProcessoRecursoCancelado.descricao;
                        }
                    });

                    $scope.$on('selectedModalItemtipoCorpoTextoModal', function (event, data) {
                        if (angular.isUndefined(data))
                            return;
                        if (angular.isUndefined(data.entidade)) {
                            $scope.data.tipoCorpoTexto = {
                                id: data.id,
                                versaoRegistro: data.versaoRegistro
                            };
                            $scope.dataModal.tipoCorpoTexto = data.descricao;
                        } else {
                            $scope.data.tipoCorpoTexto = {
                                id: data.entidade.id,
                                versaoRegistro: data.entidade.versaoRegistro
                            };
                            $scope.dataModal.tipoCorpoTexto = data.tipoCorpoTexto.descricao;
                        }
                    });


                    $scope.$watch('dataModal.origemProcesso', function (newValue, oldValue) {
                        if (angular.isUndefined(newValue))
                            return;
                        if (newValue !== oldValue && newValue === '') {
                            delete($scope.data.entidade.origemProcesso);
                            $scope.dataModal.origemProcesso = '';
                        }
                    });

                    $scope.$watch('dataModal.fluxoProcessoEdital', function (newValue, oldValue) {
                        if (angular.isUndefined(newValue))
                            return;
                        if (newValue !== oldValue && newValue === '') {
                            delete($scope.data.entidade.fluxoProcessoEdital);
                            $scope.dataModal.fluxoProcessoEdital = '';
                        }
                    });

                    $scope.$watch('dataModal.fluxoProcessoRecurso', function (newValue, oldValue) {
                        if (angular.isUndefined(newValue))
                            return;
                        if (newValue !== oldValue && newValue === '') {
                            delete($scope.data.entidade.fluxoProcessoRecurso);
                            $scope.dataModal.fluxoProcessoRecurso = '';
                        }
                    });

                    $scope.$watch('dataModal.fluxoProcessoRecursoCancelado', function (newValue, oldValue) {
                        if (angular.isUndefined(newValue))
                            return;
                        if (newValue !== oldValue && newValue === '') {
                            delete($scope.data.entidade.fluxoProcessoRecursoCancelado);
                            $scope.dataModal.fluxoProcessoRecursoCancelado = '';
                        }
                    });

                   $scope.$watch('dataModal.tipoCorpoTexto', function (newValue, oldValue) {
                        if (angular.isUndefined(newValue))
                            return;
                        if (newValue !== oldValue && newValue === '') {
                            delete($scope.data.tipoCorpoTexto);
                            $scope.dataModal.tipoCorpoTexto = '';
                        }
                    });

                    ////////////////////////////////////////////////////////////////////
                    //// Faço a validaçao dos campos obrigatorios no formulario.
                    $scope.validateForm = function () {
                        //funcionalidade interrompida

                        //se nao estiver preenchido os campos abaixo, bloqueia o botao gravar
                        if (angular.isUndefined($scope.data.entidade))
                            return true;

                        if (angular.isDefined(
                                $scope.data.entidade.origemProcesso &&
                                $scope.data.entidade.fluxoProcessoEdital &&
                                $scope.data.entidade.fluxoProcessoRecurso &&
                                $scope.data.entidade.fluxoProcessoRecursoCancelado))
                            return false;

                        // bloqueia o botao gravar;
                        return true;
                    };

                    ////////////////////////////////////////////////////////////////////
                    ///// Qndo clico no botao SALVAR
                    $scope.preSave = function () {
                        //manipulo meus dados antes de gravar
                        $scope.data.entidade.fluxoFase = $rootScope.mestre.fluxoFase.fluxoFase;
                        $scope.data.entidade.tipoCorpoTexto = $scope.data.tipoCorpoTexto.id;

                        //gravar
                        $scope.save(false, true);
                    };

                    $scope.$on('savedData', function (event, data) {
                        $scope.data.entidade = {};
                        $scope.dataModal = {};
                        $scope.resetActions();
                    });
                };
            }
        ]);
