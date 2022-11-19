/**
 *
 * @author Carlos Eduardo
 */

//Create the module
angular.module('status', ['ngRoute'])

//Variaveis que nao vareiam rsrs
        .constant("statusConfig", {
            name: 'status',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/pastatusandamentos/search',
                "gravar": 'detran-processo-administrativo/resource/pastatusandamentos/save',
                "desativar": 'detran-processo-administrativo/resource/pastatusandamentos/',
                "reativar": 'detran-processo-administrativo/resource/pastatusandamentos/reativar',
                "novo": 'detran-processo-administrativo/resource/pastatusandamentos/new'
            },
            model: [
                {field: "entidade.id", hidden: "hide"},
                {field: "entidade.versaoRegistro", hidden: "hide"},
                {field: "entidade.status.descricao", displayName: "status.label.descricao", isJson: true},
                {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
                {field: "actions", actions: [
                        {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
                        {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
                        {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"}
                    ]}
            ],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/andamentopa/status',
            i18nextResources: {
                "status": {"titulo": "Status Andamento",
                    "label": {
                        "status": "Status Andamento",
                        "descricao":"Descrição",
                    }
                }
            },
            hasMestre: true
        })

//Este é o controller Principal.
        .controller('statusCtrl', ['$scope', '$injector', 'statusConfig', 'srvDetranAbstractResourceRest',
            '$rootScope', 'srvTable', "detranModal", 'utils', '$location',
            function ($scope, $injector, config, srvRest, $rootScope, srvTable, dtnModal, utils, $location) {
                //aviso que qndo sou chamado, ainda nao tenho conteudo
                // vou ter conteudo somente qndo clicarem na minha aba
                $scope.hasData = false;

                //Somente qndo clica na tab é que devo ler os conteúdos
                $scope.$on('tabClicked', function (event, tab) {
                    if (tab !== 'status')
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
                        if (data.myName !== 'status')
                            return;
                        $scope.action(data.action, data.data);
                    });

                    //// Verifico se os dados do $scope.data foram carregados corretamente
                    //// o $emit está no mestreDetalheBasicCtrl na funcao $scope.action
                    $scope.$on('dataIsLoaded', function () {
                        $scope.dataModal.status = $scope.data.entidade.status.descricao;
                          console.log($scope.data);
                    });

                    ////////////////////////////////////////////////////////////////////
                    //// Qndo dou duplo clique ou enter no campo 
                    $scope.statusModal = function () {
                        var configModal = angular.copy(config);
                        configModal.itemsPerPage = 10;
                        configModal.limitSizePagination = true;
                        var modalConfiguration = {
                            name: 'StatusAndamentoModal',
                            msgItensVazio: "Já existe registro",
                            resourcePath: 'detran-processo-administrativo/resource/pastatuss/searchStatusPorAndamentoPA',
                            params: {
                                descricao: $scope.dataModal.status,
                                ativo: "ATIVO",
                                andamentoProcesso:$rootScope.mestre.andamentoProcesso.andamentoProcesso
                            },
                            inputModel: 'dataModal.status',
                            inputHidden: 'data.entidade.status',
                            
                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'id', hidden: "hide"},
                                    {field: 'versaoRegistro', hidden: "hide"},
                                    {field: 'descricao', displayName: "status.label.descricao"}
                                ]
                            },
                            fieldDisplayName: 'Status',
                            config: config
                            
                        };
                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                    };
                    $scope.$on('selectedModalItemStatusAndamentoModal', function (event, data) {
                        if (angular.isUndefined(data))
                            return;
                        if (angular.isUndefined(data.entidade)) {
                            $scope.data.entidade.status = {
                                id: data.id,
                                versaoRegistro: data.versaoRegistro
                            };
                            $scope.dataModal.status = data.descricao;
                        } else {
                            $scope.data.entidade.status = {
                                id: data.entidade.id,
                                versaoRegistro: data.entidade.versaoRegistro
                            };
                            $scope.dataModal.status = data.entidade.descricao;
                        }
                    });

                    $scope.$watch('dataModal.status', function (newValue, oldValue) {
                        if (angular.isUndefined(newValue))
                            return;
                        if (newValue !== oldValue && newValue === '') {
                            delete($scope.data.entidade.status);
                            $scope.dataModal.status = '';
                        }
                    });

                    ////////////////////////////////////////////////////////////////////
                    //// Faço a validaçao dos campos obrigatorios no formulario.
                    $scope.validateForm = function () {
                        //funcionalidade interrompida

                        //se nao estiver preenchido os campos abaixo, bloqueia o botao gravar
                        if (angular.isUndefined($scope.data.entidade))
                            return true;

                        if (angular.isDefined($scope.data.entidade.status ))
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
