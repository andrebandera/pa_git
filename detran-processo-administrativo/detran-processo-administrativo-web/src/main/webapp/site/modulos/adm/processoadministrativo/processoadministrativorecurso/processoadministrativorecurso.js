angular.module('processoadministrativorecurso', ['ngRoute'])

.constant("processoadministrativorecursoConfig", {
    name: 'processoadministrativorecurso',
    urls: {
        "pesquisar": 'detran-processo-administrativo/resource/consultarecursos/search'
    },
    model: [
        {field:"id", hidden: "hide"},
        {field:"entidade.tipoRecurso", displayName:"processoadministrativorecurso.label.tipoRecurso" },
        {field:"situacaoRecursoLabel", displayName:"processoadministrativorecurso.label.situacaoRecurso" },
        {field:"protocolo.numeroProtocoloMascarado", displayName:"processoadministrativorecurso.label.numeroProtocolo" },
        {field:"templateApresentacao.dataProtocolo", displayName:"processoadministrativorecurso.label.dataProtocolo" },
        {field:"resultado.resultado", displayName:"processoadministrativorecurso.label.resultado" },
        {field:"entidade.ativoLabel", displayName:"modulo.global.label.ativoLabel" },	
        {field: "actions", actions: [
          {name: "ver", icon: "global.btn.ver.icon", btn:"global.btn.ver.btn", title:"global.btn.ver.tooltip"}
        ]}
    ],
    filesPath: '/detran-processo-administrativo/site/modulos/adm/processoadministrativo/processoadministrativorecurso',
    urlPathMestre: '/adm/processoadministrativo',
    hasMestre: true,
    i18nextResources:{
        "processoadministrativorecurso":{
            "titulo":"Recursos",
            "label":{
                tipoRecurso: "Tipo Recurso",
                situacao: "Situação",
                situacaoRecurso: "Situação Recurso",
                numeroProtocolo: "Número Protocolo",
                numeroProtocoloCancelamento: "Número Protocolo Cancelamento",
                dataMovimento: "Data Protocolo",
                origemDestino: "Origem Destino",
                motivoAlegacao: "Motivo Alegação",
                indiceConhecimento: "Indica Conhecimento",
                indiceForaPrazo: "Indica Fora Prazo",
                responsavelProtocolo: "Responsável Protocolo",
                tipoSituacao: "Tipo Situação",
                formaProtocolo: "Forma Protocolo",
                dataProtocolo: "Data Protocolo",
                dataCancelamento: "Data Cancelamento",
                observacao: "Observação",
                resultado: "Resultado",
                dataParecer: "Data Parecer",
                parecer: "Parecer",
                usuario: "Usuário",
                dataRecurso: "Data Recurso"
            }
        }
    }
})

.controller('processoadministrativorecursoCtrl', ['$scope', '$injector', 'processoadministrativorecursoConfig', 'srvDetranAbstractResourceRest', '$rootScope', 'srvTable',
    function ($scope, $injector, config, srvRest, $rootScope, srvTable) {
        $injector.invoke(ctrlFiltroDtl, this, {$scope: $scope, $rootScope: $rootScope, config: config});
        $scope.showTabs = false;
        
        $scope.hasData = false;
        
        $scope.$on('tabClicked', function(event, tab){
            if (tab != 'processoadministrativorecurso') return;
            
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
                    params.id = data.data[0].value;
                    
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