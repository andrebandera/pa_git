/**
 *
 * @author Carlos Eduardo
 */

//Create the module
angular.module('andamentofluxofase', ['ngRoute'])

//Variaveis que nao vareiam rsrs
        .constant("andamentofluxofaseConfig", {
            name: 'andamentofluxofase',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/pafluxofases/search',
                "gravar": 'detran-processo-administrativo/resource/pafluxofases/save',
                "desativar": 'detran-processo-administrativo/resource/pafluxofases/',
                "reativar": 'detran-processo-administrativo/resource/pafluxofases/reativar',
                "novo": 'detran-processo-administrativo/resource/pafluxofases/new'
            },
            model: [
                {field: "entidade.id", hidden: "hide"},
                {field: "entidade.versaoRegistro", hidden: "hide"},
                {field: "entidade.prioridadeFluxoAmparo.fluxoProcesso.codigo", displayName: "andamentofluxofase.label.codigo", isJson: true},
                {field: "entidade.prioridadeFluxoAmparo.fluxoProcesso.descricao", displayName: "andamentofluxofase.label.fluxoProcesso", isJson: true},
                {field: "entidade.prioridadeFluxoAmparo.faseProcessoAdm.codigo", displayName: "andamentofluxofase.label.codigo", isJson: true},
                {field: "entidade.prioridadeFluxoAmparo.faseProcessoAdm.descricao", displayName: "andamentofluxofase.label.faseProcesso", isJson: true},
                {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
                {field: "actions", actions: [
                        {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
                        {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
                        {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"}
                    ]}
            ],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/andamentopa/andamentofluxofase',
            i18nextResources: {
                "andamentofluxofase": {"titulo": "Fluxo Fase",
                    "label": {
                        "faseProcesso": "Fase Processo",
                        "fluxoProcesso":"Fluxo Processo",
                        "codigo":"Código"
                    }
                }
            },
            hasMestre: true
        })

//Este é o controller Principal.
        .controller('andamentofluxofaseCtrl', ['$scope', '$injector', 'andamentofluxofaseConfig', 'srvDetranAbstractResourceRest',
            '$rootScope', 'srvTable', "detranModal", 'utils', '$location',
            function ($scope, $injector, config, srvRest, $rootScope, srvTable, dtnModal, utils, $location) {
                //aviso que qndo sou chamado, ainda nao tenho conteudo
                // vou ter conteudo somente qndo clicarem na minha aba
                $scope.hasData = false;

                //Somente qndo clica na tab é que devo ler os conteúdos
                $scope.$on('tabClicked', function (event, tab) {
                    if (tab !== 'andamentofluxofase')
                        return;

                    // se eu nao tenho conteudo, leio o $scope.start para criar os conteudos
                    if (!$scope.hasData) {
                        $scope.start();
                        $scope.hasData = true; // agora eu tenho conteudo, nao preciso ler mais o $scope.start
                    } else {
                        $scope.clear();
                        $scope.$broadcast('updateData', $rootScope.mestre.andamentoProcesso, true, config.name); // faco um reload dos conteudos
                    }
                });

                //Leio o conteúdo
                $scope.start = function () {
                    if ($rootScope.mestre.andamentoProcesso === undefined || $rootScope.mestre.andamentoProcesso === '') {
                        return;
                    }
                    $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
                    $injector.invoke(mestreDetalheBasicCtrl, this, {$scope: $scope, config: config});

                    $scope.urlPath = $scope.$parent.urlPath;
                    $scope.dataModal = {};


                    //DADOS DA DIRETIVA DETRAN-TABLE Q ESTA NO INDEX.HTML
                    $scope.config = config;

                    //Defino os botoes que uso na minha table
                    var actionButtons = ($scope.page === 'view') ? ['ver'] : ['ver','desativar','reativar'];
                    //funcionalidade interrompida apenas botão ver       
                    $scope.actionButtons = srvTable.actionButtons(actionButtons);

                    //getAction of buttons of table
                    // o $emit esta na detranTable
                    $scope.$on('getAction', function (event, data) {
                        if (data.myName !== 'andamentofluxofase')
                            return;
                        $scope.action(data.action, data.data);
                    });

                    //// Verifico se os dados do $scope.data foram carregados corretamente
                    //// o $emit está no mestreDetalheBasicCtrl na funcao $scope.action
                    $scope.$on('dataIsLoaded', function () {
                        $scope.dataModal.andamentofluxofase = $scope.data.entidade.andamentofluxofase.descricao;
                          console.log($scope.data);
                    });

//                    ////////////////////////////////////////////////////////////////////
//                    //// Qndo dou duplo clique ou enter no campo 
//                    $scope.andamentofluxofaseModal = function () {
//                        var configModal = angular.copy(config);
//                        configModal.itemsPerPage = 10;
//                        configModal.limitSizePagination = true;
//                        var modalConfiguration = {
//                            name: 'StatusAndamentoModal',
//                            msgItensVazio: "Já existe registro",
//                            resourcePath: 'detran-processo-administrativo/resource/paandamentofluxofases/searchStatusPorAndamentoPA',
//                            params: {
//                                descricao: $scope.dataModal.andamentofluxofase,
//                                ativo: "ATIVO",
//                                andamentoProcesso:$rootScope.mestre.andamentoProcesso.andamentoProcesso
//                            },
//                            inputModel: 'dataModal.andamentofluxofase',
//                            inputHidden: 'data.entidade.andamentofluxofase',
//                            
//                            args: {
//                                "type": "table",
//                                "model": [
//                                    {field: 'id', hidden: "hide"},
//                                    {field: 'versaoRegistro', hidden: "hide"},
//                                    {field: 'descricao', displayName: "andamentofluxofase.label.descricao"}
//                                ]
//                            },
//                            fieldDisplayName: 'Status',
//                            config: config
//                            
//                        };
//                        if (!$scope.modalIsLoading) {
//                            dtnModal.getModal(modalConfiguration, $scope);
//                        }
//                    };
//                    $scope.$on('selectedModalItemStatusAndamentoModal', function (event, data) {
//                        if (angular.isUndefined(data))
//                            return;
//                        if (angular.isUndefined(data.entidade)) {
//                            $scope.data.entidade.andamentofluxofase = {
//                                id: data.id,
//                                versaoRegistro: data.versaoRegistro
//                            };
//                            $scope.dataModal.andamentofluxofase = data.descricao;
//                        } else {
//                            $scope.data.entidade.andamentofluxofase = {
//                                id: data.entidade.id,
//                                versaoRegistro: data.entidade.versaoRegistro
//                            };
//                            $scope.dataModal.andamentofluxofase = data.entidade.descricao;
//                        }
//                    });
//
//                    $scope.$watch('dataModal.andamentofluxofase', function (newValue, oldValue) {
//                        if (angular.isUndefined(newValue))
//                            return;
//                        if (newValue !== oldValue && newValue === '') {
//                            delete($scope.data.entidade.andamentofluxofase);
//                            $scope.dataModal.andamentofluxofase = '';
//                        }
//                    });

                    ////////////////////////////////////////////////////////////////////
                    //// Faço a validaçao dos campos obrigatorios no formulario.
                    $scope.validateForm = function () {
                        //funcionalidade interrompida

                        //se nao estiver preenchido os campos abaixo, bloqueia o botao gravar
                        if (angular.isUndefined($scope.data.entidade))
                            return true;

                        if (angular.isDefined($scope.data.entidade.andamentofluxofase ))
                            return false;

                        // bloqueia o botao gravar;
                        return true;
                    };

                    ////////////////////////////////////////////////////////////////////
                    ///// Qndo clico no botao SALVAR
                    $scope.preSave = function () {
                        //manipulo meus dados antes de gravar
                        $scope.data.entidade.andamentoProcesso = $rootScope.mestre.andamentoProcesso.andamentoProcesso;
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
