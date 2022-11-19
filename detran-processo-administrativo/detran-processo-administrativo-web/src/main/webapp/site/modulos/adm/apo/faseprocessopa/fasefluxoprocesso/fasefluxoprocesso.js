/*
 * @author Carlos Eduardo
 */

//Create the module
angular.module('fasefluxoprocesso', ['ngRoute'])

//Variaveis que nao vareiam rsrs
        .constant("fasefluxoprocessoConfig", {
            name: 'fasefluxoprocesso',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/search',
                "editar": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/editar',
                "gravar": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/save',
                "desativar": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/',
                "reativar": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/reativar',
                "novo": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/new',
                "ver":'detran-processo-administrativo/resource/paprioridadefluxoamparos/ver'
            },
            model: [
                {field: "entidade.id", hidden: "hide"},
                {field: "entidade.versaoRegistro", hidden: "hide"},
                {field: "entidade.fluxoProcesso.descricao", displayName: "fasefluxoprocesso.label.fasefluxoprocesso", isJson: true},
                {field: "entidade.prioridade", displayName: "fasefluxoprocesso.label.prioridade", isJson: true},
                {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
                {field: "actions", actions: [
                        {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
                        {name: "editar", icon: "global.btn.editar.icon", btn: "global.btn.editar.btn", title: "global.btn.editar.tooltip"},
                        {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
                        {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"}
                    ]}
            ],
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/faseprocessopa/fasefluxoprocesso',
            i18nextResources: {
                "fasefluxoprocesso": {"titulo": "Fluxo Processo",
                    "label": {
                        "descricao": "Descrição",
                        "fasefluxoprocesso": "Fluxo Processo",
                        "prioridade":"Prioridade"
                    }
                }
            },
            hasMestre: true
        })

//Este é o controller Principal.
        .controller('fasefluxoprocessoCtrl', ['$scope', '$injector', 'fasefluxoprocessoConfig', 'srvDetranAbstractResourceRest',
            '$rootScope', 'srvTable', "detranModal", 'utils', '$location',
            function ($scope, $injector, config, srvRest, $rootScope, srvTable, dtnModal, utils, $location) {
                //aviso que qndo sou chamado, ainda nao tenho conteudo
                // vou ter conteudo somente qndo clicarem na minha aba
                $scope.hasData = false;
                //Somente qndo clica na tab é que devo ler os conteúdos
                $scope.$on('tabClicked', function (event, tab) {
                    if (tab !== 'fasefluxoprocesso')
                        return;

                    // se eu nao tenho conteudo, leio o $scope.start para criar os conteudos
                    if (!$scope.hasData) {
                        $scope.start();
                        $scope.hasData = true; // agora eu tenho conteudo, nao preciso ler mais o $scope.start
                    } else {
                        $scope.clear();
                        $scope.$broadcast('updateData', $rootScope.mestre.faseProcessoAdm, true, config.name); // faco um reload dos conteudos
                    }
                });

                //Leio o conteúdo
                $scope.start = function () {
                    if ($rootScope.mestre.faseProcessoAdm === undefined || $rootScope.mestre.faseProcessoAdm === '') {
                        return;
                    }
                    
                    $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
                    $injector.invoke(mestreDetalheBasicCtrl, this, {$scope: $scope, config: config});

                    $scope.urlPath = $scope.$parent.urlPath;
                    $scope.dataModal = {};


                    //DADOS DA DIRETIVA DETRAN-TABLE Q ESTA NO INDEX.HTML
                    $scope.config = config;

                    //Defino os botoes que uso na minha table
                    var actionButtons = ($scope.page === 'view') ? ['ver'] : ['ver', 'editar', 'desativar', 'reativar'];
                    //funcionalidade interrompida apenas botão ver
                    actionButtons = ['ver'];
                    $scope.actionButtons = srvTable.actionButtons(actionButtons);

                    //getAction of buttons of table
                    // o $emit esta na detranTable
                    $scope.$on('getAction', function (event, data) {
                        if (data.myName !== 'fasefluxoprocesso')
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
                        var modalConfiguration = {
                            name: 'FluxoProcessoModal',
                            resourcePath: 'detran-processo-administrativo/resource/pafluxoprocessos/search',
                            params: {
                                descricao: $scope.dataModal.fluxoProcesso,
                                ativo: "ATIVO"
                            },
                            inputModel: 'dataModal.fluxoProcesso',
                            inputHidden: 'data.entidade.fluxoProcesso',
                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'entidade.id', hidden: "hide"},
                                    {field: 'entidade.versaoRegistro', hidden: "hide"},
                                    {field: 'entidade.descricao', displayName: "fasefluxoprocesso.label.descricao"}
                                ]
                            },
                            fieldDisplayName: 'Fluxo Processo',
                            config: config
                        };
                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                    };
                };
            }
        ]);
