
			var container;
			var camera, controls, scene, renderer;
			var objects = [], plane;
			

			var raycaster = new THREE.Raycaster();
			var mouse = new THREE.Vector2(),
			offset = new THREE.Vector3(),
			INTERSECTED, SELECTED;

			controls = { enabled: true };
			function init() {

				container = $("#container")[0];
				camera = new THREE.PerspectiveCamera( 70, window.innerWidth / window.innerHeight, 1, 10000 );
				camera.position.z = 1000;

				controls = new THREE.TrackballControls( camera );
				controls.rotateSpeed = 3.0;
				controls.zoomSpeed = 1.2;
				controls.panSpeed = 0.8;
				controls.noZoom = false;
				controls.noPan = false;
				controls.staticMoving = true;
				controls.dynamicDampingFactor = 0.3;

				scene = new THREE.Scene();

				scene.add( new THREE.AmbientLight( 0x505050 ) );

				var light = new THREE.SpotLight( 0xffffff, 1.5 );
				light.position.set( 0, 500, 2000 );
				light.castShadow = true;

				light.shadowCameraNear = 200;
				light.shadowCameraFar = camera.far;
				light.shadowCameraFov = 50;

				light.shadowBias = -0.00022;
				light.shadowDarkness = 0.5;

				light.shadowMapWidth = 2048;
				light.shadowMapHeight = 2048;
		
				scene.add(light);
				var x =  window.x;
				var y = window.y;
				var z = window.z;
				console.log(parent.window.xDim);
				var geometry = new THREE.BoxGeometry(parent.window.nx, parent.window.ny, parent.window.nz);
				var boxGeometry = new THREE.BoxGeometry( 5, 5, 5 );

				

				var mesh = new THREE.Mesh( geometry, new THREE.MeshBasicMaterial( { color: 0xFF00FF, opacity: 0.5, transparent: true} ) );
				var box = new THREE.Mesh( boxGeometry, new THREE.MeshLambertMaterial( { color: 0x00FF00, opacity: 1, transparent: true} ) );
				
				var object = new THREE.BoxHelper( mesh );

				object.position.x = 0;
				object.position.y = 0;
				object.position.z = 0;

				object.rotation.x = 0;
				object.rotation.y = 0;
				object.rotation.z = 0;
				
				box.position.x = 0;
				box.position.y = 0;
				box.position.z = 0;

				box.rotation.x = 0;
				box.rotation.y = 0;
				box.rotation.z = 0;


				//box.castShadow = true;
				//box.receiveShadow = true;

				scene.add( object );
				scene.add(box);

				objects.push( object );
				objects.push(box);

				//plane = new THREE.Mesh(
					//new THREE.PlaneBufferGeometry( 2000, 2000, 8, 8 ),
					//new THREE.MeshBasicMaterial( { color: 0x000000, opacity: 0.25, transparent: true } )
				//);
				//plane.visible = false;
				//scene.add( plane );

				renderer = new THREE.WebGLRenderer( { antialias: true, alpha : true} );
				//renderer.setClearColor( 0x000000 );
				//renderer.setPixelRatio( window.devicePixelRatio );
				renderer.setSize( window.innerWidth, window.innerHeight );
				renderer.sortObjects = true;

				//renderer.shadowMapEnabled = true;
				//renderer.shadowMapType = THREE.PCFShadowMap;

				container.appendChild( renderer.domElement );

				renderer.domElement.addEventListener( 'mousemove', onDocumentMouseMove, false );
				renderer.domElement.addEventListener( 'mousedown', onDocumentMouseDown, false );
				renderer.domElement.addEventListener( 'mouseup', onDocumentMouseUp, false );
				window.addEventListener( 'resize', onWindowResize, false );
			}

			function onWindowResize() {
				camera.aspect = window.innerWidth / window.innerHeight;
				camera.updateProjectionMatrix();
				renderer.setSize( window.innerWidth, window.innerHeight );
			}

			function onDocumentMouseMove( event ) {
				event.preventDefault();
				mouse.x = ( event.clientX / window.innerWidth ) * 2 - 1;
				mouse.y = - ( event.clientY / window.innerHeight ) * 2 + 1;
				raycaster.setFromCamera( mouse, camera );
				if ( SELECTED ) {
					var intersects = raycaster.intersectObject( plane );
					SELECTED.position.copy( intersects[ 0 ].point.sub( offset ) );
					return;
				}

				var intersects = raycaster.intersectObjects( objects );
				if ( intersects.length > 0 ) {
					if ( INTERSECTED != intersects[ 0 ].object ) {
						if ( INTERSECTED ) INTERSECTED.material.color.setHex( INTERSECTED.currentHex );
						INTERSECTED = intersects[ 0 ].object;
						INTERSECTED.currentHex = INTERSECTED.material.color.getHex();
						INTERSECTED.material.color.setHex( 255 );
						plane.position.copy( INTERSECTED.position );
						plane.lookAt( camera.position );
					}
					container.style.cursor = 'pointer';
				} else {
					if ( INTERSECTED ) INTERSECTED.material.color.setHex( INTERSECTED.currentHex );
					INTERSECTED = null;
					container.style.cursor = 'auto';
				}
			}

			function onDocumentMouseDown( event ) {
				event.preventDefault();
				var vector = new THREE.Vector3( mouse.x, mouse.y, 0.5 ).unproject( camera );
			raycaster.setFromCamera( mouse, camera );
//			var raycaster = new THREE.Raycaster( camera.position, vector.sub( camera.position ).normalize() );
				var intersects = raycaster.intersectObjects( objects );
				if ( intersects.length > 0 ) {
					controls.enabled = false;
					SELECTED = intersects[ 0 ].object;
					var intersects = raycaster.intersectObject( plane );
					offset.copy( intersects[ 0 ].point ).sub( plane.position );
					container.style.cursor = 'move';
				}
			}

			function stopControl( event ) {
					controls.enabled = false;
			}

			function startControl( event ) {
					controls.enabled = true;
			}

			function onDocumentMouseUp( event ) {
				event.preventDefault();
				controls.enabled = true;
				if ( INTERSECTED ) {
					plane.position.copy( INTERSECTED.position );
					SELECTED = null;
				}
				container.style.cursor = 'auto';
			}

			//

			function animate() {
				requestAnimationFrame( animate );
				render();
			}

			function render() {
				controls.update();
				renderer.render( scene, camera );
			}
			
