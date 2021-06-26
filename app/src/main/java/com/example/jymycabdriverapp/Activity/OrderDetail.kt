package com.example.jymycabdriverapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.jymycabdriverapp.R
import com.example.jymycabdriverapp.ui.dashboard.DashboardFragment
import com.google.android.material.textfield.TextInputLayout
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import org.conscrypt.Conscrypt
import retrofit2.Call
import retrofit2.Response
import timber.log.Timber
import java.security.Security

class OrderDetail : AppCompatActivity(), OnMapReadyCallback {
    lateinit var textinput_otp: TextInputLayout

    lateinit var layout_otp: LinearLayout
    var client : MapboxDirections?= null
    lateinit var origin: Point
    lateinit var mapboxmap: MapboxMap //https://youtu.be/MvM2KytTG9U
    lateinit var bttn_pickup: Button

    private val ROUTE_LAYER_ID = "route-layer-id"
    private val ROUTE_SOURCE_ID = "route-source-id"
    private val ICON_LAYER_ID = "icon-layer-id"
    private val ICON_SOURCE_ID = "icon-source-id"
    private val RED_PIN_ICON_ID = "red-pin-icon-id"

    private var currentRoute: DirectionsRoute? = null
    private var pickup: Point? = null
    private var destination: Point? = null
    var context: Context? = null

    lateinit var mapView: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        Mapbox.getInstance(this, resources.getString(R.string.mapbox_access_token))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
        textinput_otp = findViewById(R.id.textinput_otp)
        bttn_pickup = findViewById(R.id.bttn_pickup)
        mapView = findViewById(R.id.mapView);
        Security.insertProviderAt(Conscrypt.newProvider(), 1);

        layout_otp = findViewById(R.id.layout_otp)
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        bttn_pickup.isEnabled = false

        textinput_otp.getEditText()?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            @SuppressLint("ResourceAsColor")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length < 1) {
                    bttn_pickup.setBackgroundResource(R.drawable.backgroundotpedittext)
                    bttn_pickup.setTextColor(R.color.light_black1)
                    bttn_pickup.isEnabled = false

                }
                else if (s.length > 0) {
                    bttn_pickup.isEnabled = true
                    bttn_pickup.setBackgroundResource(R.drawable.button_round_1)
                    bttn_pickup.setTextColor(Color.WHITE)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

    }

    var locationComponent: LocationComponent? = null
    var uiSettings: UiSettings? = null
    override fun onMapReady(mapboxMap: MapboxMap) {
        if (mapboxMap != null) {
            this.mapboxmap = mapboxMap
        }
//       mapboxMap.setStyle(Style.MAPBOX_STREETS,Style.OnStyleLoaded {
//
//       })

        mapboxMap.setStyle(Style.MAPBOX_STREETS){
            uiSettings = mapboxMap.uiSettings
            uiSettings!!.isAttributionEnabled = false
            uiSettings!!.isLogoEnabled = false
            uiSettings!!.isCompassEnabled = false
            uiSettings!!.isRotateGesturesEnabled = true
            // val lastKnownLocation = mapboxMap.locationComponent.lastKnownLocation

//           val lat =  lastKnownLocation!!.latitude
//            val lang = lastKnownLocation!!.longitude
            // Toast.makeText(requireContext(),""+lat,Toast.LENGTH_LONG).show()

           // locationComponent = mapboxMap.locationComponent
//            locationComponent!!.activateLocationComponent(
//                LocationComponentActivationOptions
//                    .builder(this, it)
//                    .useDefaultLocationEngine(true)
//                    .locationEngineRequest(
//                        LocationEngineRequest.Builder(750)
//                            .setFastestInterval(750)
//                            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
//                            .build()
//                    )
//                    .build()
//            )


            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return@setStyle
            }
//            locationComponent?.isLocationComponentEnabled = true
//            locationComponent?.renderMode = RenderMode.COMPASS
//            locationComponent?.cameraMode = CameraMode.TRACKING
           // Toast.makeText(this,""+locationComponent!!.lastKnownLocation?.latitude,Toast.LENGTH_LONG).show()

            val position =  CameraPosition.Builder()
                .target(
                    LatLng(28.61724927383127,77.3735563527)
                )

                .zoom(15.0)
                .tilt(20.0)
                .build()
            pickup = Point.fromLngLat(77.3735563527,28.61724927383127)
            destination = Point.fromLngLat(77.37860963453853,28.619980513057182);
            bttn_pickup.setOnClickListener {


            }
            initSource(it);

            initLayers(it);

// Get the directions route from the Mapbox Directions API
              getRoute(mapboxMap, pickup!!, destination!!);
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100)
//            currentLocation.setOnClickListener(View.OnClickListener { view: View? ->
//                mapboxMap.animateCamera(
//                    CameraUpdateFactory.newCameraPosition(position),
//                    1000
//                )
//            })


        }


//        mapboxMap?.setStyle(Style.MAPBOX_STREETS)
//        mapboxMap.animateCamera(
//            CameraUpdateFactory.newCameraPosition(
//                CameraPosition.Builder()
//                    .target(LatLng(28.61921040435677,  77.36959373335871))
//                    .zoom(10.0)
//                    .tilt(45.0)
//                    .build()
//            ),
//            10000
//
//        )

//        mapboxMap.uiSettings.isAttributionEnabled = false
//        mapboxMap.uiSettings.isLogoEnabled = false

    }
    private fun initSource(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource(ROUTE_SOURCE_ID))
        val iconGeoJsonSource = GeoJsonSource(
            ICON_SOURCE_ID, FeatureCollection.fromFeatures(
                arrayOf<Feature>(
                    Feature.fromGeometry(
                        Point.fromLngLat(pickup!!.longitude(),pickup!!.latitude()
                        )
                    ),
                    Feature.fromGeometry(
                        Point.fromLngLat(destination!!.longitude(),destination!!.latitude()
                        )
                    )
                )
            )
        )
        loadedMapStyle.addSource(iconGeoJsonSource)
    }
    private fun initLayers(loadedMapStyle: Style) {
        val routeLayer = LineLayer(ROUTE_LAYER_ID, ROUTE_SOURCE_ID)

// Add the LineLayer to the map. This layer will display the directions route.
        routeLayer.setProperties(
            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
            PropertyFactory.lineWidth(5f),
            PropertyFactory.lineColor(Color.parseColor("#009688"))
        )
        loadedMapStyle.addLayer(routeLayer)

// Add the red marker icon image to the map
        loadedMapStyle.addImage(
            RED_PIN_ICON_ID, BitmapUtils.getBitmapFromDrawable(
                resources.getDrawable(R.drawable.ic_baseline_location_on_24)
            )!!
        )

// Add the red marker icon SymbolLayer to the map
        loadedMapStyle.addLayer(
            SymbolLayer(ICON_LAYER_ID, ICON_SOURCE_ID).withProperties(
                PropertyFactory.iconImage(RED_PIN_ICON_ID),
                PropertyFactory.iconIgnorePlacement(true),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconOffset(arrayOf(0f, -9f))
            )
        )
    }

    private fun getRoute(mapboxMap: MapboxMap?, origin: Point, destination: Point) {
        client = MapboxDirections.builder()
            .origin(origin)
            .destination(destination)
            .overview(DirectionsCriteria.OVERVIEW_FULL)
            .profile(DirectionsCriteria.PROFILE_DRIVING)
            .accessToken(resources.getString(R.string.mapbox_access_token))
            .build()
        client!!.enqueueCall(object : retrofit2.Callback<DirectionsResponse?> {
            override fun onResponse(
                call: Call<DirectionsResponse?>?,
                response: Response<DirectionsResponse?>
            ) {
// You can get the generic HTTP info about the response
                Timber.d("Response code: " + response.code())
                if (response.body() == null) {
                    Timber.e("No routes found, make sure you set the right user and access token.")
                    return
                } else if (response.body()!!.routes().size < 1) {
                    Timber.e("No routes found")
                    return
                }
                currentRoute = response.body()!!.routes().get(0)
//                navigatelocation?.setOnClickListener() {
//                    this?.let {
//                        val simulateRoute = true
//                        val options: NavigationLauncherOptions = NavigationLauncherOptions.builder()
//                            .directionsRoute(currentRoute)
//                            .shouldSimulateRoute(simulateRoute)
//                            .build()
//                        NavigationLauncher.startNavigation(this@Confirm_Booking, options)
//                    }
//                }
//                val navigationLauncherOptions = NavigationLauncherOptions.builder() //1
//                    .directionsRoute(currentRoute) //2
//                    .shouldSimulateRoute(true) //3
//                    .build()
//
//                 NavigationLauncher.startNavigation(this@Confirm_Booking, navigationLauncherOptions) //
//


                mapboxMap?.getStyle(object : Style.OnStyleLoaded {
                    override fun onStyleLoaded(style: Style) {

                        // Retrieve and update the source designated for showing the directions route
                        val source: GeoJsonSource? = style.getSourceAs(ROUTE_SOURCE_ID)

                        // Create a LineString with the directions route's geometry and
                        // reset the GeoJSON source for the route LineLayer source
                        if (source != null) {
                            source.setGeoJson(
                                LineString.fromPolyline(
                                    currentRoute!!.geometry()!!,
                                    Constants.PRECISION_6
                                )
                            )
                        }
                    }
                })
            }

            override fun onFailure(call: Call<DirectionsResponse?>?, throwable: Throwable) {
                Timber.e("Error: " + throwable.message)
                Toast.makeText(
                    this@OrderDetail, "Error: " + throwable.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (client != null) {
            client!!.cancelCall();
        }
        mapView.onDestroy();

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState!!)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


}