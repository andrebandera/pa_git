angular.module('processoadministrativocontrolecnh', ['ngRoute'])

.constant("processoadministrativocontrolecnhConfig", {
    name: 'processoadministrativocontrolecnh',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/processoadministrativocontrolecnhs/search'
    },
    model: [
        {field:"id", hidden: "hide"},
        {field: "entidade.acao", hidden: "hide"},
        {field: "entidade.cnhControle.id", hidden: "hide"}, 
        {field: "movimento.id", hidden: "hide"}, 
        {field: "entidade.cnhControlePessoa.cpfEntrega", displayName: "processoadministrativocontrolecnh.label.cpfEntrega"}, 
        {field: "entidade.cnhControlePessoa.nomeEntrega", displayName: "processoadministrativocontrolecnh.label.nomeEntrega"}, 
        {field: "entidade.cnhControle.numeroRegistro", displayName: "processoadministrativocontrolecnh.label.numeroRegistro"}, 
        {field: "entidade.cnhControle.numeroCnh", displayName: "processoadministrativocontrolecnh.label.numeroCnh"}, 
        {field: "entidade.postoAtendimento.descricao", displayName: "processoadministrativocontrolecnh.label.postoAtendimento"},
        {field: "acaoLabel", displayName: "processoadministrativocontrolecnh.label.acao"},
        {field: "actions", actions: [
          {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"}
        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativocontrolecnh',
    urlPathMestre: '/adm/processoadministrativo',
    hasMestre: true,
    i18nextResources:{
        "processoadministrativocontrolecnh":{
            "titulo":"Controle CNH",
            "label":{
                cpfEntrega:"CPF Entrega",
                nomeEntrega:"Nome Entrega",
                numeroRegistro:"Número Registro",
                numeroProtocolo:"Número Protocolo",
                numeroCnh: "Número CNH",
                postoAtendimento: "Posto Atendimento",
                acao: "Ação",
                formaProtocolo: "Forma Protocolo",
                responsavel: "Responsável",
                representanteLegal: "Representante Legal",
                observacao: "Observação",
                observacaoCNH: "Observação CNH",
                nome: "Nome",
                cpf: "CPF",
                municipio:"Município",
                dataEntrega: "Data",
                formaEntrega: "Forma Entrega",
                telefone: "Telefone para Contato",
                usuario: "Usuário"
            }
        }
    }
})

.controller('processoadministrativocontrolecnhCtrl', ['$scope', '$injector', 'processoadministrativocontrolecnhConfig', 'srvDetranAbstractResourceRest', '$rootScope', 'srvTable',
    function ($scope, $injector, config, srvRest, $rootScope, srvTable) {
        $injector.invoke(ctrlFiltroDtl, this, {$scope: $scope, $rootScope: $rootScope, config: config});
        $scope.showTabs = false;
        
        $scope.hasData = false;
        
        $scope.$on('tabClicked', function(event, tab){
            if (tab != 'processoadministrativocontrolecnh') return;
            
            if (!$scope.hasData) {
              $scope.start();
              $scope.hasData = true;
              
            } else {
                $scope.clear();
                $scope.clear();
                $scope.$broadcast('updateData', $rootScope.mestre.processoadministrativo, true, config.name);
            }
        });

        $scope.start = function() {
            var mestreID = angular.copy($rootScope.mestre.processoadministrativo);
            
            if (mestreID == undefined || mestreID == '') return;
            
            $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
            $injector.invoke(mestreDetalheBasicCtrl, this, {$scope: $scope, config: config});
        
            $scope.urlPath = $scope.$parent.urlPath;
            $scope.dataModal = {};
            
            $scope.mestreID = mestreID;
            $scope.config = config;
            
            var actionButtons = ['ver'];
            $scope.actionButtons = srvTable.actionButtons(actionButtons);
            
            $scope.$on('doSearch', function() {
                $scope.showTabs = false;
                $scope.clear();

                $scope.$broadcast('updateData', $rootScope.mestre.processoadministrativo, true, config.name);
            });

            $scope.$on('getAction', function(event, data) { 
                if (data.action == 'ver') {
                    $scope.showTabs = true;
                    $scope.resetActions();
                    $scope.setAction('view', true);
                    
                    var params = {}
                    params.id = data.data[3].value;
                    srvRest.pesquisar(config.urls.pesquisar, params).then(function(retorno) {
                        if (retorno.error != '') {
                            $scope.growl.addErrorMessage(retorno.error);
                            return;
                        }
                        $scope.data = retorno.entity[0];
                        $rootScope.loading = false;
                        $scope.$broadcast('dataIsLoaded');
                    }, function(err) {
                        $scope.growl.addErrorMessage(err.error);
                    });
                } else {
                    $scope.action(data.action, data.data);
                }
            });
        };
    }
]);