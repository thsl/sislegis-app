angular.module('sislegisapp').factory(
		'ElaboracaoNormativaResource',
		function($resource) {
			var resource = $resource('../rest/elaboracaonormativa/:ElaboracaoNormativaId', {
						ElaboracaoNormativaId : '@id'
					}, {
						'queryAll' : {
							method : 'GET',
							isArray : true
						},
						'query' : {
							method : 'GET',
							isArray : false
						},
						'update' : {
							method : 'PUT'
						},
						'tipos' : {
							url : "../rest/elaboracaonormativa/tipos",
							method : 'GET',
							isArray : true
						},
						'identificacoes' : {
							url : "../rest/elaboracaonormativa/identificacoes",
							method : 'GET',
							isArray : true
						},
						'normas' : {
							url : "../rest/elaboracaonormativa/normas",
							method : 'GET',
							isArray : true
						},
						'situacoes' : {
							url : "../rest/elaboracaonormativa/situacoes",
							method : 'GET',
							isArray : true
						},						
						'searchElaboracaoNormativa' : {
							url : "../rest/elaboracaonormativa/searchElaboracaoNormativa",
						    params: {
							        tipo: '@tipo',
							        nup: '@nup'
							      },
							method : 'GET',
							isArray : true
						}						
					});
			return resource;
		});