/**
 * @author: Carlos Eduardo
 * @since 04/07/2019
 */

//Create the module
angular.module('fluxofase', ['ngRoute'])

//Configuration
.constant("fluxofaseConfig", {
    label: 'Fluxo Fase',
    name: 'fluxofase',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/pafluxofases/search',
        "gravar": 'detran-processo-administrativo/resource/pafluxofases/save',
        "novo": 'detran-processo-administrativo/resource/pafluxofases/new',
        "desativar": 'detran-processo-administrativo/resource/pafluxofases/',
        "reativar": 'detran-processo-administrativo/resource/pafluxofases/reativar',
        "editar": 'detran-processo-administrativo/resource/pafluxofases/editar'
    },
    model: [
        {field: "id", hidden: "hide"},
        {field: "versaoRegistro", hidden: "hide"},
        {field: "entidadeFull", hidden: "hide"},
        {field: "entidade.andamentoProcesso.descricao", displayName: "fluxofase.label.fluxoprocesso", isJson: true},
        {field: "entidade.andamentoProcesso.codigo", displayName: "Código"},
        {field: "entidade.tipoAndamento", displayName: "fluxofase.label.tipoAndamento"},
        {field: "entidade.prioridade", displayName: "Prioridade"},
        {field: "entidade.ativoLabel", displayName: "modulo.global.label.ativoLabel"},
        {field: "actions", actions: [
            {name: "aumentar", icon: "global.btn.aumentar.icon", btn: "global.btn.aumentar.btn", title: "global.btn.aumentar.tooltip"},
            {name: "diminuir", icon: "global.btn.diminuir.icon", btn: "global.btn.diminuir.btn", title: "global.btn.diminuir.tooltip"},
            {name: "ver", icon: "global.btn.ver.icon", btn: "global.btn.ver.btn", title: "global.btn.ver.tooltip"},
            {name: "editar", icon: "global.btn.editar.icon", btn: "global.btn.editar.btn", title: "global.btn.editar.tooltip"},
            {name: "reativar", icon: "global.btn.reativar.icon", btn: "global.btn.reativar.btn", title: "global.btn.reativar.tooltip"},
            {name: "desativar", icon: "global.btn.desativar.icon", btn: "global.btn.desativar.btn", title: "global.btn.desativar.tooltip"}
        ]}
    ],
    hasMestre: true,
    filesPath: '/detran-processo-administrativo/site/modulos/adm/apo/fluxoprocesso/fluxoprocessofase/fluxofase',
    i18nextResources: {
        "fluxofase": {
            "titulo": "Andamento Processo", 
            "label": {
                "fluxoprocesso": "Andamento Processo",
                "prioridade": "Prioridade ",
                "tipoandamento":"Tipo Andamento",
                "descricao": "Descrição",
                "tipoAndamento": "Tipo Andamento"
            }
        }
    },
    itemsPerPage: 10
})

//Controller Principal
.controller("fluxofaseCtrl", ['$scope', '$injector', 'fluxofaseConfig', 'detranModal',
'srvDetranAbstractResourceRest', '$rootScope', 'srvTable', 'utils','srvMestreDtl',
    function($scope, $injector, config, dtnModal, srvRest, $rootScope, srvTable, utils,srvMestreDtl) {
            
    $injector.invoke(ctrlFiltroDtl, this, {$scope: $scope, $rootScope: $rootScope, config: config});
    //aviso que qndo sou chamado, ainda nao tenho conteudo
    // vou ter conteudo somente qndo clicarem na minha aba FASE PROCESSO
    $scope.hasData = false;
    $scope.showTabs = false;
		
    //Somente qndo clica na tab é que devo ler os conteúdos
    $scope.$on('tabClicked', function(event, tab) {
        if (tab != 'fluxofase') return;

        // se eu nao tenho conteudo, leio o $scope.start para criar os conteudos
        if (!$scope.hasData) {
            $scope.start();
            $scope.setAction('view', true);
            $scope.hasData = true; // agora eu tenho conteudo, nao preciso ler mais o $scope.start
        } else {
            $scope.clear();
            var mestreID = angular.copy($rootScope.mestre.prioridadeFluxoAmparo);
            mestreID.ativo = mestreID.ativo || "ATIVO";
            $scope.$broadcast('updateData', mestreID, true, config.name); // faco um reload dos conteudos
        }
    });

    //Leio o conteúdo
    $scope.start = function() {
        var mestreID = angular.copy($rootScope.mestre.prioridadeFluxoAmparo);
        if (mestreID == undefined || mestreID == '') return;

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
        $scope.$on('getAction' + config.name, function(event, data) {

            $scope.clear();

            if (data.action == 'aumentar') {
                $scope.carregar('detran-processo-administrativo/resource/pafluxofases/aumentarprioridade', data.data[2].value).then(function(retorno) {
                    $scope.$broadcast('updateData', {});
                    $rootScope.loading = false;
                });

            } else if (data.action == 'diminuir') {
                $scope.carregar('detran-processo-administrativo/resource/pafluxofases/diminuirprioridade', data.data[2].value).then(function(retorno) {
                    $scope.$broadcast('updateData', {});
                    $rootScope.loading = false;
                });
            }
        });

        //// Verifico se os dados do $scope.data foram carregados corretamente
        //// o $emit está no mestreDetalheBasicCtrl na funcao $scope.action
       //Load que carrega o dado a ser inserido no Geral quando clicar em editar, popula dataModal.alguma coisa.
        $scope.$on('dataIsLoaded', function() {
            $scope.dataModal.andamentoProcessoDescricaoValue = $scope.data.entidade.andamentoProcesso.descricao;
            $rootScope.mestre.fluxoFase = srvMestreDtl.setMestre('fluxoFase', $scope.data.entidade);
            $rootScope.mestre.fluxoAtual = srvMestreDtl.setMestre('fluxoAtual', $scope.data.entidade);
            $rootScope.mestre.fluxoFase.andamentoProcesso = {
                descricao: $scope.data.entidade.andamentoProcesso.descricao
            };
            $scope.showTabs = true;
        });

        // Qndo clico no botao NOVO
        $scope.new = function() {
            $scope.resetActions();
            $scope.clear();
            $scope.setAction('novo', true);
            $rootScope.loading = true;

            var params = {entidade: {prioridadeFluxoAmparo: mestreID.prioridadeFluxoAmparo}};

            srvRest.pesquisar(config.urls.novo, params).then(function(retorno) {
                $scope.clearData();
                $scope.data.entidade.prioridade = retorno.entity[0].entidade.prioridade;
                $rootScope.loading = false;
            });
        };

        // Faço a validaçao do meu formulario
        $scope.validateForm = function() {
            if (!$scope.dataChanged()) return true;
            
            if (angular.isUndefined($scope.data.entidade)) return true;

            //se nao estiver preenchido os campos abaixo, bloqueia o botao gravar
            if ($scope.edit) {
                if ($scope.data.entidade.prioridade) return false;
            }

            if($scope.novo) {
                if ($scope.data.entidade.andamentoProcesso && 
                    ($scope.data.entidade.prioridade && 
                     $scope.data.entidade.andamentoProcesso &&
                     $scope.data.entidade.tipoAndamento)) 
                    return false;
            }

            // bloqueia o botao gravar;
            return true;    
        };

        $scope.$on('doSearch', function(){
            $scope.$broadcast('updateData', $scope.mestreID, true, config.name);
        });

        $scope.$on('doClear', function(){
            $scope.mestreID = {};
            $scope.mestreID = angular.copy(mestreID);
            $scope.$broadcast('primeiraPagina', $scope.mestreID, true, config.name);
        });

        // Qndo clico no botao SALVAR
        $scope.preSave = function() {
            //trato os meus dados
            //Se for novo
            if ($scope.data.id == undefined) {
                var newData = {};
                newData.entidade = {};
                newData.entidade.andamentoProcesso = $scope.data.entidade.andamentoProcesso;
                newData.entidade.prioridade = $scope.data.entidade.prioridade;
                newData.entidade.prioridadeFluxoAmparo = mestreID.prioridadeFluxoAmparo;
                newData.entidade.tipoAndamento = $scope.data.entidade.tipoAndamento;

                $scope.data = {};
                $scope.data = newData;
            }
        //save
            $scope.save(false);
        };

        $scope.$on('savedData', function(event, data) {
            $scope.resetActions();
        });

        // Qndo faço duplo clique ou enter no campo andamentoProcessoDescricaoValue
        $scope.andamentoProcessoDescricaoModal = function(event) {
            event.preventDefault();
            var configModal = angular.copy(config);
            configModal.itemsPerPage = 10;
            configModal.limitSizePagination = true;
            var modalConfiguration = {
                name: "andamentoProcessoDescricaoModal",
                resourcePath: 'detran-processo-administrativo/resource/paandamentoprocessos/searchAndamentoPorFluxoFase',
                params: {
                    prioridadeFluxoAmparo:$rootScope.mestre.prioridadeFluxoAmparo.prioridadeFluxoAmparo,
                    descricao: $scope.dataModal.andamentoProcessoDescricaoValue
                },
                inputModel: 'dataModal.andamentoProcessoDescricaoValue',
                inputHidden: 'data.entidade.andamentoProcesso',
                args: {
                    "type": "table",
                    "model": [
                        {field: 'id', hidden: "hide"},
                        {field: 'versaoRegistro', hidden: "hide"},
                        {field: 'descricao', displayName: "Descricao"},
                        {field: 'codigo', displayName: "Código"}
                    ],
                    "title": 'Andamento Processo'
                },
                fieldDisplayName: 'descricao',
                config: configModal
            };
            if (!$scope.modalIsLoading) {
                dtnModal.getModal(modalConfiguration, $scope);
            };
            return false;
        };
    
        $scope.$watch('dataModal.andamentoProcessoDescricaoValue', function(newValue, oldValue) {
            if (newValue == undefined) return;

            if (newValue != oldValue && newValue == '') {
                $scope.data.entidade.andamentoProcesso = {};
                $scope.dataModal.andamentoProcessoDescricaoValue = '';
            }
        });
    };
}]);