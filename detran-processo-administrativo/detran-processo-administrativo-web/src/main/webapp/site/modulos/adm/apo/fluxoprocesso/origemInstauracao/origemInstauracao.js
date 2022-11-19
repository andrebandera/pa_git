/**
 *
 * @author Carlos Eduardo
 */

//Create the module
angular.module('origemInstauracao', ['ngRoute'])

//Variaveis que nao vareiam rsrs
        .constant("origemInstauracaoConfig", {
            name: 'origemInstauracao',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/pafluxoorigems/search',
                "gravar": 'detran-processo-administrativo/resource/pafluxoorigems/save',
                "desativar": 'detran-processo-administrativo/resource/pafluxoorigems/',
                "reativar": 'detran-processo-administrativo/resource/pafluxoorigems/reativar',
                "novo": 'detran-processo-administrativo/resource/pafluxoorigems/new'
            },
            model: [
                {field: "entidade.id", hidden: "hide"},
                {field: "entidade.versaoRegistro", hidden: "hide"},
                {field: "entidade.origemInstauracao.regra", displayName: "origemInstauracao.label.regra"},
                {field: "entidade.origemInstauracao.descricao", displayName: "origemInstauracao.label.descricao", isJson: true},
                {field: "indiceFluxoInicialLabel", displayName: "origemInstauracao.label.indiceFluxoInicial"},
                {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
                {field: "actions", actions: [
                        {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
                        {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
                        {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"}
                    ]}
            ],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/origemInstauracao',
            i18nextResources: {
                "origemInstauracao": {"titulo": "Origem Instauração",
                    "label": {
                        "origemInstauracao": "Origem Instauracão",
                        "descricao":"Descrição",
                        "regra": "Regra",
                        "indiceFluxoInicial": "Indicativo Fluxo Inicial",
                        "motivo": "Origem Instauração"
                    }
                }
            },
            hasMestre: true
        })

//Este é o controller Principal.
        .controller('origemInstauracaoCtrl', ['$scope', '$injector', 'origemInstauracaoConfig', 'srvDetranAbstractResourceRest',
            '$rootScope', 'srvTable', "detranModal", 'utils', '$location',
            function ($scope, $injector, config, srvRest, $rootScope, srvTable, dtnModal, utils, $location) {
                //aviso que qndo sou chamado, ainda nao tenho conteudo
                // vou ter conteudo somente qndo clicarem na minha aba
                $scope.hasData = false;

                //Somente qndo clica na tab é que devo ler os conteúdos
                $scope.$on('tabClicked', function (event, tab) {
                    if (tab !== 'origemInstauracao')
                        return;

                    // se eu nao tenho conteudo, leio o $scope.start para criar os conteudos
                    if (!$scope.hasData) {
                        $scope.start();
                        $scope.hasData = true; // agora eu tenho conteudo, nao preciso ler mais o $scope.start
                    } else {
                        $scope.clear();
                        $scope.$broadcast('updateData', $rootScope.mestre.fluxoProcesso, true, config.name); // faco um reload dos conteudos
                    }
                });

                //Leio o conteúdo
                $scope.start = function () {
                    if ($rootScope.mestre.fluxoProcesso === undefined || $rootScope.mestre.fluxoProcesso === '') {
                        return;
                    }
                    $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
                    $injector.invoke(mestreDetalheBasicCtrl, this, {$scope: $scope, config: config});

                    $scope.urlPath = $scope.$parent.urlPath;
                    $scope.dataModal = {};


                    //DADOS DA DIRETIVA DETRAN-TABLE Q ESTA NO INDEX.HTML
                    $scope.config = config;

                    //Defino os botoes que uso na minha table
                    var actionButtons = ($scope.page === 'view') ? ['ver'] : ['ver','editar','desativar','reativar'];
                    //funcionalidade interrompida apenas botão ver       
                    $scope.actionButtons = srvTable.actionButtons(actionButtons);

                    //getAction of buttons of table
                    // o $emit esta na detranTable
                    $scope.$on('getAction', function (event, data) {
                        if (data.myName !== 'origemInstauracao')
                            return;
                        $scope.action(data.action, data.data);
                    });

                    //// Verifico se os dados do $scope.data foram carregados corretamente
                    //// o $emit está no mestreDetalheBasicCtrl na funcao $scope.action
                    $scope.$on('dataIsLoaded', function () {
                        $scope.dataModal.origemInstauracao = $scope.data.entidade.origemInstauracao.descricao;
                          console.log($scope.data);
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
                            resourcePath: 'detran-processo-administrativo/resource/apoioorigeminstauracaos/searchOrigemInstauracaoPorFluxoProcesso',
                            params: {
                                descricao: $scope.dataModal.origemInstauracao,
                                fluxoProcesso:$rootScope.mestre.fluxoProcesso.fluxoProcesso
                            },
                            inputModel: 'dataModal.origemInstauracao',
                            inputHidden: 'data.entidade.origemInstauracao',
                            
                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'id', hidden: "hide"},
                                    {field: 'versaoRegistro', hidden: "hide"},
                                    {field: 'regra', displayName: "origemInstauracao.label.regra"},
                                    {field: 'descricao', displayName: "origemInstauracao.label.descricao"}
                                ]
                            },
                            fieldDisplayName: 'origemInstauracao',
                            config: config
                            
                        };
                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                    };
                    $scope.$on('selectedModalItemOrigemInstauracaoModal', function (event, data) {
                        if (angular.isUndefined(data))
                            return;
                        if (angular.isUndefined(data.entidade)) {
                            $scope.data.entidade.origemInstauracao = {
                                id: data.id,
                                versaoRegistro: data.versaoRegistro
                            };
                            $scope.dataModal.origemInstauracao = data.descricao;
                        } else {
                            $scope.data.entidade.origemInstauracao = {
                                id: data.entidade.id,
                                versaoRegistro: data.entidade.versaoRegistro
                            };
                            $scope.dataModal.origemInstauracao = data.entidade.descricao;
                        }
                    });

                    $scope.$watch('dataModal.origemInstauracao', function (newValue, oldValue) {
                        if (angular.isUndefined(newValue))
                            return;
                        if (newValue !== oldValue && newValue === '') {
                            delete($scope.data.entidade.origemInstauracao);
                            $scope.dataModal.origemInstauracao = '';
                        }
                    });

                    ////////////////////////////////////////////////////////////////////
                    //// Faço a validaçao dos campos obrigatorios no formulario.
                    $scope.validateForm = function () {
                        //funcionalidade interrompida

                        //se nao estiver preenchido os campos abaixo, bloqueia o botao gravar
                        if (angular.isUndefined($scope.data.entidade))
                            return true;

                        if (angular.isDefined($scope.data.entidade.origemInstauracao ))
                            return false;

                        // bloqueia o botao gravar;
                        return true;
                    };

                    ////////////////////////////////////////////////////////////////////
                    ///// Qndo clico no botao SALVAR
                    $scope.preSave = function () {
                        //manipulo meus dados antes de gravar
                        $scope.data.entidade.fluxoProcesso = $rootScope.mestre.fluxoProcesso.fluxoProcesso;
                        
                        console.log($scope.data.entidade);
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
