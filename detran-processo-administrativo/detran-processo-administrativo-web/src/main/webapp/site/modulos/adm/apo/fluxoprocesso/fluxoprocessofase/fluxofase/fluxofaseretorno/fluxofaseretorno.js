/**
 *
 * @author Carlos Eduardo
 * Data: 18/07/2019
 */

//Create the module
angular.module('fluxofaseretorno', ['ngRoute'])

//Variaveis que nao vareiam rsrs
        .constant("fluxofaseretornoConfig", {
            name: 'fluxofaseretorno',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/pafluxofaseretornos/search',
                "gravar": 'detran-processo-administrativo/resource/pafluxofaseretornos/save',
                "desativar": 'detran-processo-administrativo/resource/pafluxofaseretornos/',
                "reativar": 'detran-processo-administrativo/resource/pafluxofaseretornos/reativar',
                "novo": 'detran-processo-administrativo/resource/pafluxofaseretornos/new'
            },
            model: [
                {field: "entidade.id", hidden: "hide"},
                {field: "entidade.versaoRegistro", hidden: "hide"},
                {field: "entidade.fluxoAtual.andamentoProcesso.descricao", displayName: "fluxofaseretorno.label.fluxoAtual"},
                {field: "entidade.fluxoRetorno.andamentoProcesso.descricao", displayName: "fluxofaseretorno.label.fluxoRetorno"},
                {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
                {field: "actions", actions: [
                        {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
                        {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
                        {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"}
                    ]}
            ],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/fluxofaseretorno',
            i18nextResources: {
                "fluxofaseretorno": {"titulo": "Fluxo Fase Retorno",
                    "label": {
                        "fluxofaseretorno": "Fluxo Fase Retorno",
                        "descricao": "Descrição",
                        "fluxoAtual": "Fluxo Atual",
                        "fluxoRetorno": "Fluxo Destino"
                        }
                }
            },
            hasMestre: true
        })

//Este é o controller Principal.
        .controller('fluxofaseretornoCtrl', ['$scope', '$injector', 'fluxofaseretornoConfig', 'srvDetranAbstractResourceRest',
            '$rootScope', 'srvTable', "detranModal", 'utils', '$location',
            function ($scope, $injector, config, srvRest, $rootScope, srvTable, dtnModal, utils, $location) {
                //aviso que qndo sou chamado, ainda nao tenho conteudo
                // vou ter conteudo somente qndo clicarem na minha aba
                $scope.hasData = false;


                //Somente qndo clica na tab é que devo ler os conteúdos
                $scope.$on('tabClicked', function (event, tab) {
                    if (tab !== 'fluxofaseretorno')
                        return;

                    // se eu nao tenho conteudo, leio o $scope.start para criar os conteudos
                    if (!$scope.hasData) {
                        $scope.start();
                        $scope.hasData = true; // agora eu tenho conteudo, nao preciso ler mais o $scope.start
                    } else {
                        $scope.clear();
                        var mestreID = angular.copy($rootScope.mestre.fluxoAtual);
                        mestreID.ativo = "ATIVO";
                        $scope.$broadcast('updateData', mestreID, true, config.name); // faco um reload dos conteudos
                    }
                });

                //Leio o conteúdo
                $scope.start = function () {
                    var mestreID = angular.copy($rootScope.mestre.fluxoAtual);
                    if ($rootScope.mestre.fluxoAtual === undefined || $rootScope.mestre.fluxoAtual === '') {
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
                        if (data.myName !== 'fluxofaseretorno')
                            return;
                        $scope.action(data.action, data.data);
                    });

                    //// Verifico se os dados do $scope.data foram carregados corretamente
                    //// o $emit está no mestreDetalheBasicCtrl na funcao $scope.action
                    $scope.$on('dataIsLoaded', function () {
                        $scope.dataModal.fluxoRetorno = $scope.data.entidade.fluxoRetorno.andamentoProcesso.descricao;
                    });

                    ////////////////////////////////////////////////////////////////////
                    //// Qndo dou duplo clique ou enter no campo 
                    $scope.fluxoRetornoModal = function () {
                        var configModal = angular.copy(config);
                        configModal.itemsPerPage = 10;
                        configModal.limitSizePagination = true;
                        var modalConfiguration = {
                            name: 'fluxoRetornoModal',
                            msgItensVazio: "Já existe registro",
                            resourcePath: 'detran-processo-administrativo/resource/pafluxofases/search',
                            params: {
                                descricaoRetorno: $scope.dataModal.fluxoRetorno,
                                ativo: "ATIVO"
                            },
                            inputModel: 'dataModal.fluxoRetorno',
                            inputHidden: 'data.entidade.fluxoRetorno',

                            "windowClass": 'app-modal-window-lg',
                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'entidade.id', hidden: "hide"},
                                    {field: 'entidade.versaoRegistro', hidden: "hide"},
                                    {field: 'fluxoProcessoLabel', displayName: "Fluxo Processo"},
                                    {field: 'faseProcessoAdmLabel', displayName: "Fase Processo"},
                                    {field: 'andamentoProcessoLabel', displayName: "Andamento Processo"},
                                ],
                                "title": 'Andamento Processo Destino'
                            },
                            fieldDisplayName: 'Andamento Processo Destino',
                            config: configModal

                        };
                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                    };

                    
                    $scope.$on('selectedModalItemfluxoRetornoModal', function (event, data) {
                        if (angular.isUndefined(data))
                            return;
                        if (angular.isUndefined(data.entidade)) {
                            $scope.data.entidade.fluxoRetorno = {
                                id: data.id,
                                versaoRegistro: data.versaoRegistro
                            };
                            $scope.dataModal.fluxoRetorno = data.andamentoProcessoLabel;
                        } else {
                            $scope.data.entidade.fluxoRetorno = {
                                id: data.entidade.id,
                                versaoRegistro: data.entidade.versaoRegistro
                            };
                            $scope.dataModal.fluxoRetorno = data.andamentoProcessoLabel;
                        }
                    });
                    
                    $scope.$watch('dataModal.fluxoRetorno', function (newValue, oldValue) {
                        if (angular.isUndefined(newValue))
                            return;
                        if (newValue !== oldValue && newValue === '') {
                            delete($scope.data.entidade.fluxoRetorno);
                            $scope.dataModal.fluxoRetorno = '';
                        }
                    });
                    //////// Faço a validaçao dos campos obrigatorios no formulario.
                    $scope.validateForm = function () {
                        //funcionalidade interrompida

                        //se nao estiver preenchido os campos abaixo, bloqueia o botao gravar
                        if (angular.isUndefined($scope.data.entidade))
                            return true;

                        if (angular.isDefined(
                                $scope.data.entidade.fluxoRetorno))
                            return false;

                        // bloqueia o botao gravar;
                        return true;
                    };

                    ////////////////////////////////////////////////////////////////////
                    ///// Qndo clico no botao SALVAR
                    $scope.preSave = function () {
                        //manipulo meus dados antes de gravar
                        $scope.data.entidade.fluxoAtual = $rootScope.mestre.fluxoAtual.fluxoAtual;
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
