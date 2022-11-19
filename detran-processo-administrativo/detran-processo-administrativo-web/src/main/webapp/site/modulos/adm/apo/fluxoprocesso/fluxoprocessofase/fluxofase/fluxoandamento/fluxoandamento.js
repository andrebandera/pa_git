/**
 *
 * @author Carlos Eduardo
 * Data: 18/07/2019
 */

//Create the module
angular.module('fluxoandamento', ['ngRoute'])

//Variaveis que nao vareiam rsrs
        .constant("fluxoandamentoConfig", {
            name: 'fluxoandamento',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/pafluxoandamentos/search',
                "gravar": 'detran-processo-administrativo/resource/pafluxoandamentos/save',
                "desativar": 'detran-processo-administrativo/resource/pafluxoandamentos/',
                "reativar": 'detran-processo-administrativo/resource/pafluxoandamentos/reativar',
                "novo": 'detran-processo-administrativo/resource/pafluxoandamentos/new'
            },
            model: [
                {field: "entidade.id", hidden: "hide"},
                {field: "entidade.versaoRegistro", hidden: "hide"},
                {field: "entidade.fluxoFase.andamentoProcesso.descricao", displayName: "fluxoandamento.label.fluxoAtual"},
                {field: "entidade.fluxoProcesso.descricao", displayName: "fluxoandamento.label.fluxoRetorno"},
                {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
                {field: "actions", actions: [
                        {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
                        {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
                        {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"}
                    ]}
            ],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase/fluxoandamento',
            i18nextResources: {
                "fluxoandamento": {"titulo": "Fluxo Destino",
                    "label": {
                        "fluxoandamento": "Fluxo Destino",
                        "descricao": "Descrição",
                        "fluxoAtual": "Fluxo Fase",
                        "fluxoRetorno": "Fluxo Destino"
                        }
                }
            },
            hasMestre: true
        })

//Este é o controller Principal.
        .controller('fluxoandamentoCtrl', ['$scope', '$injector', 'fluxoandamentoConfig', 'srvDetranAbstractResourceRest',
            '$rootScope', 'srvTable', "detranModal", 'utils', '$location',
            function ($scope, $injector, config, srvRest, $rootScope, srvTable, dtnModal, utils, $location) {
                //aviso que qndo sou chamado, ainda nao tenho conteudo
                // vou ter conteudo somente qndo clicarem na minha aba
                $scope.hasData = false;


                //Somente qndo clica na tab é que devo ler os conteúdos
                $scope.$on('tabClicked', function (event, tab) {
                    if (tab !== 'fluxoandamento')
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
                        if (data.myName !== 'fluxoandamento')
                            return;
                        $scope.action(data.action, data.data);
                    });

                    //// Verifico se os dados do $scope.data foram carregados corretamente
                    //// o $emit está no mestreDetalheBasicCtrl na funcao $scope.action
                    $scope.$on('dataIsLoaded', function () {
                        $scope.dataModal.fluxoProcesso = $scope.data.entidade.fluxoProcesso.descricao;
                    });

                    ////////////////////////////////////////////////////////////////////
                    //// Qndo dou duplo clique ou enter no campo 
                    $scope.fluxoProcessoModal = function () {
                        var configModal = angular.copy(config);
                        configModal.itemsPerPage = 10;
                        configModal.limitSizePagination = true;
                        var modalConfiguration = {
                            name: 'fluxoProcessoModal',
                            msgItensVazio: "Já existe registro",
                            resourcePath: 'detran-processo-administrativo/resource/pafluxoandamentos/search',
                            params: {
                                descricaoFluxoProcesso: $scope.dataModal.fluxoProcesso,
                                ativo: "ATIVO"
                            },
                            inputModel: 'dataModal.fluxoProcesso',
                            inputHidden: 'data.entidade.fluxoProcesso',

                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'entidade.id', hidden: "hide"},
                                    {field: 'entidade.versaoRegistro', hidden: "hide"},
                                    {field: 'entidade.fluxoProcesso.codigo', displayName: "Código"},
                                    {field: 'entidade.fluxoProcesso.descricao', displayName: "Fluxo Processo"},
                                    
                                    
                                ],
                                "title": 'Fluxo Processo Destino'
                            },
                            fieldDisplayName: 'Fluxo Processo Destino',
                            config: configModal

                        };
                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                    };

                    
                    $scope.$on('selectedModalItemfluxoProcessoModal', function (event, data) {
                        if (angular.isUndefined(data))
                            return;
                        if (angular.isUndefined(data.entidade)) {
                            $scope.data.entidade.fluxoProcesso = {
                                id: data.id,
                                versaoRegistro: data.versaoRegistro
                            };
                            $scope.dataModal.fluxoProcesso = data.descricao;
                        } else {
                            $scope.data.entidade.fluxoProcesso = {
                                id: data.entidade.id,
                                versaoRegistro: data.entidade.versaoRegistro
                            };
                            $scope.dataModal.fluxoProcesso = data.descricao;
                        }
                    });
                    
                    $scope.$watch('dataModal.fluxoProcesso', function (newValue, oldValue) {
                        if (angular.isUndefined(newValue))
                            return;
                        if (newValue !== oldValue && newValue === '') {
                            delete($scope.data.entidade.fluxoProcesso);
                            $scope.dataModal.fluxoProcesso = '';
                        }
                    });
                    //////// Faço a validaçao dos campos obrigatorios no formulario.
                    $scope.validateForm = function () {
                        //funcionalidade interrompida

                        //se nao estiver preenchido os campos abaixo, bloqueia o botao gravar
                        if (angular.isUndefined($scope.data.entidade))
                            return true;

                        if (angular.isDefined(
                                $scope.data.entidade.fluxoProcesso))
                            return false;

                        // bloqueia o botao gravar;
                        return true;
                    };

                    ////////////////////////////////////////////////////////////////////
                    ///// Qndo clico no botao SALVAR
                    $scope.preSave = function () {
                        //manipulo meus dados antes de gravar
                        $scope.data.entidade.fluxoFase = $rootScope.mestre.fluxoFase.fluxoFase;
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
