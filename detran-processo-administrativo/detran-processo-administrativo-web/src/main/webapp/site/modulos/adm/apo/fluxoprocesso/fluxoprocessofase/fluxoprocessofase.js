/**
 * @author: Carlos Eduardo
 * @since 04/07/2019
 */

//Create the module
angular.module('fluxoprocessofase', ['ngRoute'])

//Configuration
        .constant("fluxoprocessofaseConfig", {
            label: 'Fluxo Processo',
            name: 'fluxoprocessofase',
            urls: {
                "pesquisar": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/search',
                "gravar": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/save',
                "novo": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/new',
                "desativar": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/',
                "reativar": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/reativar',
                "editar": 'detran-processo-administrativo/resource/paprioridadefluxoamparos/editar'
            },
            model: [
                {field: "id", hidden: "hide"},
                {field: "versaoRegistro", hidden: "hide"},
                {field: "entidadeFull", hidden: "hide"},
                {field: "entidade.faseProcessoAdm.descricao", displayName: "fluxoprocessofase.label.fluxoprocesso", isJson: true},
                {field: "entidade.faseProcessoAdm.codigo", displayName: "Código"},
                {field: "entidade.prioridade", displayName: "Prioridade"},
                {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
                {field: "actions", actions: [
                        {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
                        {name: "aumentar", icon: "global.btn.aumentar.icon", btn: "global.btn.aumentar.btn", title: "global.btn.aumentar.tooltip"},
                        {name: "diminuir", icon: "global.btn.diminuir.icon", btn: "global.btn.diminuir.btn", title: "global.btn.diminuir.tooltip"},
                        {name: "editar", icon: "global.btn.editar.icon", btn: "global.btn.editar.btn", title: "global.btn.editar.tooltip"},
                        {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"},
                        {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"},
                    ]}
            ],
            hasMestre: true,
            filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase',
            i18nextResources: {
                "fluxoprocessofase": {
                    "titulo": "Fase Processo",
                    "label": {
                        "fluxoprocesso": "Fase Processo",
                        "prioridade": "Prioridade ",
                        "descricao": "Descrição"
                    }
                }
            },
            itemsPerPage: 10
        })

//Controller Principal
        .controller("fluxoprocessofaseCtrl", ['$scope', '$injector', 'fluxoprocessofaseConfig', 'detranModal',
            'srvDetranAbstractResourceRest', '$rootScope', 'srvTable', 'utils', 'srvMestreDtl', 'dialog', 'growl',
            function ($scope, $injector, config, dtnModal, srvRest, $rootScope, srvTable, utils, srvMestreDtl, dialog, growl) {

                $injector.invoke(ctrlFiltroDtl, this, {$scope: $scope, $rootScope: $rootScope, config: config});
                //aviso que qndo sou chamado, ainda nao tenho conteudo
                // vou ter conteudo somente qndo clicarem na minha aba Andamento PROCESSO
                $scope.hasData = false;
                $scope.showTabs = false;

                //Somente qndo clica na tab é que devo ler os conteúdos
                $scope.$on('tabClicked', function (event, tab) {
                    if (tab != 'fluxoprocessofase')
                        return;

                    // se eu nao tenho conteudo, leio o $scope.start para criar os conteudos
                    if (!$scope.hasData) {
                        $scope.start();
                        $scope.setAction('view', true);
                        $scope.hasData = true; // agora eu tenho conteudo, nao preciso ler mais o $scope.start
                    } else {
                        $scope.clear();
                        var mestreID = angular.copy($rootScope.mestre.fluxoProcesso);
                        mestreID.ativo = mestreID.ativo || "ATIVO";
                        $scope.$broadcast('updateData', mestreID, true, config.name); // faco um reload dos conteudos
                    }
                });

                //Leio o conteúdo
                $scope.start = function () {
                    var mestreID = angular.copy($rootScope.mestre.fluxoProcesso);
                    if (mestreID == undefined || mestreID == '')
                        return;

                    $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
                    $injector.invoke(mestreDetalheBasicCtrl, this, {$scope: $scope, config: config});

                    mestreID.ativo = mestreID.ativo || "ATIVO";

                    $scope.urlPath = $scope.$parent.urlPath;
                    $scope.dataModal = {};
                    $scope.data.entidade = {};

                    //DADOS DA DIRETIVA DETRAN-TABLE Q ESTA NO INDEX.HTML
                    $scope.config = config;
                    $scope.mestreID = angular.copy(mestreID);

                    //Defino os botoes que uso na minha table
                    var actionButtons = ($scope.page == 'view') ? ['ver'] : ['aumentar', 'diminuir', 'editar', 'desativar', 'reativar'];
                    $scope.actionButtons = srvTable.actionButtons(actionButtons);

                    //getAction of buttons of table
                    // o $emit esta na detranTable
                    $scope.$on('getAction' + $scope.config.name, function (event, data) {

                        if (data.action == 'aumentar') {
                            $scope.carregar('detran-processo-administrativo/resource/paprioridadefluxoamparos/aumentarprioridade', data.data[2].value).then(function (retorno) {
                                $scope.$broadcast('updateData', {});
                                $rootScope.loading = false;
                            });

                        } else if (data.action == 'diminuir') {
                            $scope.carregar('detran-processo-administrativo/resource/paprioridadefluxoamparos/diminuirprioridade', data.data[2].value).then(function (retorno) {
                                $scope.$broadcast('updateData', {});
                                $rootScope.loading = false;
                            });
                        }
                    });

                    //// Verifico se os dados do $scope.data foram carregados corretamente
                    //// o $emit está no mestreDetalheBasicCtrl na funcao $scope.action
                    //Load que carrega o dado a ser inserido no Geral quando clicar em editar, popula dataModal.alguma coisa.
                    $scope.$on('dataIsLoaded', function () {

                        $rootScope.mestre.prioridadeFluxoAmparo = srvMestreDtl.setMestre('prioridadeFluxoAmparo', $scope.data.entidade);
                        $scope.showTabs = true;
                        $scope.dataModal.faseProcessoAdmDescricaoValue = $scope.data.entidade.faseProcessoAdm.descricao;

                    });

                    // Qndo clico no botao NOVO
                    $scope.new = function () {
                        $scope.resetActions();
                        $scope.clear();
                        $scope.setAction('novo', true);
                        $rootScope.loading = true;

                        var params = {entidade: {fluxoProcesso: mestreID.fluxoProcesso}};

                        srvRest.pesquisar(config.urls.novo, params).then(function (retorno) {
                            $scope.clearData();
                            $scope.data.entidade.prioridade = retorno.entity[0].entidade.prioridade;
                            $rootScope.loading = false;
                        });
                    };

                    // Faço a validaçao do meu formulario
                    $scope.validateForm = function () {
                        if (!$scope.dataChanged())
                            return true;

                        if (angular.isUndefined($scope.data.entidade))
                            return true;

                        //se nao estiver preenchido os campos abaixo, bloqueia o botao gravar
                        if ($scope.edit) {
                            if ($scope.data.entidade.prioridade)
                                return false;
                        }

                        if ($scope.novo) {
                            if ($scope.data.entidade.faseProcessoAdm
                                    && ($scope.data.entidade.prioridade && $scope.data.entidade.faseProcessoAdm))
                                return false;
                        }

                        // bloqueia o botao gravar;
                        return true;
                    };

                    $scope.$on('doSearch', function () {
                        $scope.$broadcast('updateData', $scope.mestreID, true, config.name);
                    });

                    $scope.$on('doClear', function () {
                        $scope.mestreID = {};
                        $scope.mestreID = angular.copy(mestreID);
                        $scope.$broadcast('primeiraPagina', $scope.mestreID, true, config.name);
                    });

                    // Qndo clico no botao SALVAR
                    $scope.preSave = function () {
                        //trato os meus dados
                        //Se for novo
                        if ($scope.data.id == undefined) {
                            var newData = {};
                            newData.entidade = {};
                            newData.entidade.faseProcessoAdm = $scope.data.entidade.faseProcessoAdm;
                            newData.entidade.prioridade = $scope.data.entidade.prioridade;
                            newData.entidade.fluxoProcesso = mestreID.fluxoProcesso;

                            $scope.data = {};
                            $scope.data = newData;
                        }
                        //save
                        $scope.save(false);
                    };

                    $scope.$on('savedData', function (event, data) {
                        $scope.resetActions();
                    });

                    // Qndo faço duplo clique ou enter no campo faseProcessoAdmDescricaoValue
                    $scope.faseProcessoAdmDescricaoModal = function (event) {
                        event.preventDefault();
                        var configModal = angular.copy(config);
                        configModal.itemsPerPage = 10;
                        configModal.limitSizePagination = true;
                        var modalConfiguration = {
                            name: "faseProcessoAdmDescricaoModal",
                            resourcePath: 'detran-processo-administrativo/resource/pafaseprocessoadms/searchFasePorFluxoProcesso',
                            params: {
                                fluxoProcesso: $rootScope.mestre.fluxoProcesso.fluxoProcesso,
                                descricao: $scope.dataModal.faseProcessoAdmDescricaoValue
                            },
                            inputModel: 'dataModal.faseProcessoAdmDescricaoValue',
                            inputHidden: 'data.entidade.faseProcessoAdm',
                            args: {
                                "type": "table",
                                "model": [
                                    {field: 'id', hidden: "hide"},
                                    {field: 'versaoRegistro', hidden: "hide"},
                                    {field: 'descricao', displayName: "Descrição"},
                                    {field: 'codigo', displayName: "Código"}
                                ],
                                "title": 'Fase Processo'
                            },
                            fieldDisplayName: 'descricao',
                            config: configModal
                        };
                        if (!$scope.modalIsLoading) {
                            dtnModal.getModal(modalConfiguration, $scope);
                        }
                        ;
                        return false;
                    };

                    $scope.$watch('dataModal.faseProcessoAdmDescricaoValue', function (newValue, oldValue) {
                        if (newValue == undefined)
                            return;

                        if (newValue != oldValue && newValue == '') {
                            $scope.data.entidade.faseProcessoAdm = {};
                            $scope.dataModal.faseProcessoAdmDescricaoValue = '';
                        }
                    });
                };
            }]);