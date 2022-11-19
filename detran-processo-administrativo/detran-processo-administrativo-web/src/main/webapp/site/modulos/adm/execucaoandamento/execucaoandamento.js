angular.module('execucaoandamento', ['ngRoute'])

.constant("execucaoandamentoConfig", {
    name: 'execucaoandamento',
    urls: {
        "gravar":                           'detran-processo-administrativo/resource/execucaoandamentos/save',
        "novo":                             'detran-processo-administrativo/resource/execucaoandamentos/new',
        "buscarprocessoadministrativo":     'detran-processo-administrativo/resource/execucaoandamentos/buscarprocessoadministrativo',
        "buscarListaAndamentos":     'detran-processo-administrativo/resource/execucaoandamentos/buscarlistaandamentos'
    },
    filesPath: '/detran-processo-administrativo/site/modulos/adm/execucaoandamento',
    urlPath: '/adm/execucaoandamento',
    i18nextResources: {
        "execucaoandamento":{
            "titulo":"Ajustar Processo Administrativo", 
            "label": {
                "numeroProcesso":"Nº Processo", 
                "andamento": "Andamento (Execução em Lote)",
                "quantidadeLote": "Quantidade Lote (máx. 1000)",
                "fluxoProcessoAtual":"Fluxo Processo Atual", 
                "andamentoAtual":"Andamento Atual",
                "novoFluxoProcesso":"Novo Fluxo Processo", 
                "novoAndamento": "Novo Andamento",
                "tipoProcesso": "Tipo Processo",
                "usuarioAlteracaoLabel": "Usuário Alteração"
            }
        }
    }
})

.config(["$routeProvider", "execucaoandamentoConfig", function ($rp, config) {
  var path      = config.filesPath;
  var urlPath   = config.urlPath;
  var name      = config.name;

  $rp
    .when(urlPath, {templateUrl: path + '/index.html', controller: name + 'Create', label: 'execucaoandamento.titulo'});
}])

.controller('execucaoandamentoCtrl', ["$scope", "$injector", "execucaoandamentoConfig",
    function ($scope, $injector, config) {
        $injector.invoke(basicCtrl, this, {$scope: $scope, config: config});
    }
])

.controller('execucaoandamentoCreate', ['$scope', '$controller', '$injector',
    function($scope, $controller, $injector){
        $scope.page = 'create';
        $controller('execucaoandamentoCustomCtrl', {$scope: $scope});
    }
])

.controller('execucaoandamentoCustomCtrl', ["$scope", "$controller", "execucaoandamentoConfig", "$injector", "growl",
    function($scope, ctrl, config, $injector, growl) {
        
        ctrl('execucaoandamentoCtrl', {$scope: $scope});
        $injector.invoke(selectCtrl, this, {$scope: $scope});
         
        $scope.data = {
            alterar:{},
            executar:{}
            
        };
        

        $scope.novoAndamentoValido = false;
        $scope.buscouProcesso = false;
        $scope.alterouFluxo = false;
        
        $scope.limpar = function () {
                if (angular.isDefined($scope.data)
                && angular.isDefined($scope.data.alterar.numeroProcesso) && $scope.buscouProcesso==true) {
                    $scope.data.alterar.codigoDescricaoFluxoProcessoAtual = null;
                    $scope.data.alterar.codigoDescricaoAndamentoAtual = null;
                    $scope.data.alterar.novoFluxoProcesso = null;
                    $scope.data.alterar.novoAndamento = null;
                    $scope.buscouProcesso = false;
                    $scope.novoAndamentoValido = false;
            }
        }
        
        $scope.validaNovoAndamento = function(){
            if($scope.data.alterar.novoAndamento){
                $scope.novoAndamentoValido = true;
            }else{
                $scope.novoAndamentoValido = false;
            }
        }
        
        $scope.habilitaAndamento = function(novoFluxoProcesso){
            if(novoFluxoProcesso){
                $scope.carregar(config.urls.buscarListaAndamentos,novoFluxoProcesso).then(function(retorno){
                    if (angular.isDefined(retorno.error) && retorno.error != "") {
                        detranUtil.showMsg(growl, retorno.error);
                    } else if (retorno.warning != "" && angular.isDefined(retorno.warning)) {
                        detranUtil.showMsgWarn(growl, retorno.warning);
                    } else {
                        $scope.data.alterar.novoAndamento="";
                        $scope.novoAndamentoValido = false;
                        $scope.novoAndamentoSelect = retorno.objectResponse.listValue;
                    }
                });
                $scope.alterouFluxo = true;
            }
            else{
                $scope.data.alterar.novoAndamento="";
                $scope.alterouFluxo = false;
            }
            
        }
        
        $scope.buscarProcessoAdministrativo = function (numeroProcesso) {
            
            if (detranUtil.ehBrancoOuNulo(numeroProcesso)){
                $scope.buscouProcesso = false;
                return;
            }
            
            $scope.buscouProcesso = true;
            
            $scope.carregar(config.urls.buscarprocessoadministrativo, {numeroProcesso: numeroProcesso}).then(function (retorno) {
                
                if (retorno.error != "") {
                    $scope.buscouProcesso = false;
                    detranUtil.showMsg(growl, retorno.error);
                } else if (retorno.warning != "" && angular.isDefined(retorno.warning)) {
                    detranUtil.showMsgWarn(growl, retorno.warning);
                } else {
                    var retornoData = retorno.objectResponse;

                    $scope.novoFluxoProcessoSelect = retornoData.listaFluxo.listValue;

                    $scope.data.alterar.codigoDescricaoFluxoProcessoAtual   = retornoData.codigoDescricaoFluxoProcessoAtual;
                    $scope.data.alterar.codigoDescricaoAndamentoAtual       = retornoData.codigoDescricaoAndamentoAtual;
                }
            });
        };
        
        $scope.executarAndamento = function(){
            
            console.log("$scope.data", $scope.data);
            
            $scope.data.executar.acaoFuncionalidadeAjustaProcessoAdministrativo = 'EXECUTAR_ANDAMENTO';
            
            if (!angular.isUndefined($scope.data)) {
                if (detranUtil.ehBrancoOuNulo($scope.data.executar.andamento)) {
                    $scope.data.executar.andamento = null;
                } else {
                    $scope.data.executar.andamento = angular.copy(angular.fromJson($scope.data.executar.andamento));
                }
            }
            
            $scope.data = angular.copy($scope.data.executar);
            
            $scope.save(true, true);
        };
        
        $scope.alterarAndamento = function(){
            
            $scope.data.alterar.acaoFuncionalidadeAjustaProcessoAdministrativo = 'ALTERAR_ANDAMENTO';
            
            if (!angular.isUndefined($scope.data)) {
                  
                if (detranUtil.ehBrancoOuNulo($scope.data.alterar.novoAndamento)) {
                    $scope.data.alterar.novoAndamento = null;
                } else {
                    $scope.data.alterar.novoAndamento = angular.copy(angular.fromJson($scope.data.alterar.novoAndamento));
                }
                
                if (detranUtil.ehBrancoOuNulo($scope.data.alterar.novoFluxoProcesso)) {
                    $scope.data.alterar.novoFluxoProcesso = null;
                } else {
                    $scope.data.alterar.novoFluxoProcesso = angular.copy(angular.fromJson($scope.data.alterar.novoFluxoProcesso));
                }
            }
            
            $scope.data = angular.copy($scope.data.alterar);
            
            $scope.save(true, true);
        };
        
        $scope.validateForm = function() {
                
            if (!$scope.dataChanged()) 
                return true;

            if (!angular.isUndefined($scope.data)) {

                if (!angular.isUndefined($scope.data.entidade)) {

                    if (!detranUtil.ehBrancoOuNulo($scope.data.entidade.numeroProcesso)) {
                        return false;
                    }
                }
            }

            return true;
        };
    }
]);
