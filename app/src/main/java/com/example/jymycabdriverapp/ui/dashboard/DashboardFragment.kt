package com.example.jymycabdriverapp.ui.dashboard

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.example.jymycabdriverapp.Activity.OrderAcceptActivity
import com.example.jymycabdriverapp.R
import com.example.jymycabdriverapp.databinding.FragmentDashboardBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.mapbox.android.core.location.*
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.core.constants.Constants
import com.mapbox.core.exceptions.ServicesException
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponent
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.*
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import org.conscrypt.Conscrypt
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.lang.ref.WeakReference
import java.security.Security
import java.util.*


class DashboardFragment : Fragment(),OnMapReadyCallback, PermissionsListener {

    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    lateinit var bttn_viewdetails: Button
    var client: MapboxDirections? = null
    lateinit var origin: Point
    lateinit var mapboxmap: MapboxMap

    lateinit var mapView: MapView
    lateinit var currentLocation: FloatingActionButton
    lateinit var pickUpText: TextView
    lateinit var destinationText: TextView
    lateinit var permissionsManager: PermissionsManager
    lateinit var all_drivers: TabItem
    lateinit var fav_drivers: TabItem


    val REQUEST_CODE_AUTOCOMPLETE = 1

    lateinit var location: Location
    private var locationEngine: LocationEngine? = null
    private var locationLayerPlugin: LocationLayerPlugin? = null


    var tabLayout: TabLayout? = null
    var frameLayout: FrameLayout? = null
    var fragment: Fragment? = null
    var fragmentTransaction: FragmentTransaction? = null


    lateinit var pickUpLatLang: LatLng
    lateinit var destinationLatLang: LatLng
    lateinit var bttn_booking: Button
    private val DEFAULT_INTERVAL_IN_MILLISECONDS = 1000L
    private val DEFAULT_MAX_WAIT_TIME = DEFAULT_INTERVAL_IN_MILLISECONDS * 5


    private val ROUTE_LAYER_ID = "route-layer-id"
    private val ROUTE_SOURCE_ID = "route-source-id"
    private val ICON_LAYER_ID = "icon-layer-id"
    private val ICON_SOURCE_ID = "icon-source-id"
    private val RED_PIN_ICON_ID = "red-pin-icon-id"

    private var currentRoute: DirectionsRoute? = null
    private var pickup: Point? = null
    private var destination: Point? = null
    private val callback = LocationChangeListeningActivityLocationCallback(this)


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context?.let { Mapbox.getInstance(it, resources.getString(R.string.mapbox_access_token)) }
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mapView = root.findViewById(R.id.mapView);
        currentLocation = root.findViewById(R.id.currentlocation);
        bttn_viewdetails = root.findViewById(R.id.bttn_accept);


        Security.insertProviderAt(Conscrypt.newProvider(), 1);

        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);



        bttn_viewdetails.setOnClickListener {
            val intent = Intent(requireContext(), OrderAcceptActivity::class.java)
            startActivity(intent)

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    var locationComponent: LocationComponent? = null
    var uiSettings: UiSettings? = null
    override fun onMapReady(mapboxMap: MapboxMap) {
        val lm = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false
        try {
            gps_enabled = Objects.requireNonNull(lm).isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ignored: Exception) {
        }
        try {
            network_enabled =
                Objects.requireNonNull(lm).isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ignored: Exception) {
        }
        if (PermissionsManager.areLocationPermissionsGranted(requireContext()) or gps_enabled && network_enabled) {
            mapboxmap = mapboxMap
            mapboxmap.setStyle(Style.MAPBOX_STREETS) { style: Style? ->
                uiSettings = mapboxMap.uiSettings
                uiSettings!!.isAttributionEnabled = false
                uiSettings!!.isLogoEnabled = false
                uiSettings!!.isCompassEnabled = false
                uiSettings!!.isRotateGesturesEnabled = false

//                locationComponent = mapboxMap.locationComponent
//                locationComponent!!.activateLocationComponent(
//                    LocationComponentActivationOptions
//                        .builder(requireContext(), style!!)
//                        .useDefaultLocationEngine(true)
//                        .locationEngineRequest(
//                            LocationEngineRequest.Builder(750)
//                                .setFastestInterval(750)
//                                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
//                                .build()
//                        )
//                        .build()
//                )
//
//                locationComponent!!.isLocationComponentEnabled = true
//                locationComponent!!.renderMode = RenderMode.COMPASS
//
//                val position =
//                    CameraPosition.Builder()
//                        .target(LatLng(28.61724927383127,77.3735563527))
//                        .zoom(18.0)
//                        .tilt(20.0)
//                        .build()
//                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 100)
////                val position =
////                    CameraPosition.Builder()
////                        .target(LatLng(28.61724927383127,77.3735563527))
////                        .zoom(18.0)
////                        .tilt(20.0)
////                        .build()
//                mapboxMap.animateCamera(
//                    CameraUpdateFactory.newCameraPosition(
//                        position
//                    ), 100
//                )

                val stringOne = activity?.getIntent()?.getStringExtra("destination")
                Toast.makeText(context,""+stringOne,Toast.LENGTH_LONG).show()

                enableLocationComponent(style!!)
                initDroppedMarkerpickup(style)
                initDroppedMarkerdestination(style)
                initDottedLineSourceAndLayer(style)

                getRoute(mapboxMap)
//                currentLocation.setOnClickListener { view: View? ->
//                    mapboxMap.animateCamera(
//                        CameraUpdateFactory.newCameraPosition(position),
//                        1000
//                    )
//                }
//                setPickUpButton.setOnClickListener(View.OnClickListener { view: View? ->
//                    val centerPos =
//                        mapboxMap.cameraPosition.target
//                    onPickUp(style, mapboxMap, centerPos)
//                })
//                setDestinationButton.setOnClickListener(View.OnClickListener { view: View? ->
//                    val centerPos =
//                        mapboxMap.cameraPosition.target
//                    onDestination(style, mapboxMap, centerPos)
//                })
            }
        } else {

        }
    }
    private fun initDottedLineSourceAndLayer(loadedMapStyle: Style) {
        loadedMapStyle.addSource(GeoJsonSource("SOURCE_ID"))
        loadedMapStyle.addLayerBelow(
            LineLayer(
                "DIRECTIONS_LAYER_ID", "SOURCE_ID"
            ).withProperties(
                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                PropertyFactory.lineWidth(6f),
                PropertyFactory.lineColor(resources.getColor(R.color.colorPrimary))
            ), "LAYER_BELOW_ID"
        )
    }
    private fun onPickUp(style: Style, mapboxMap: MapboxMap, centerPos: LatLng) {
        // fetchNearDriver(centerPos.latitude, centerPos.longitude, service, style)
        val position = CameraPosition.Builder()
            .target(LatLng(centerPos.latitude, centerPos.longitude))
            .zoom(15.0)
            .tilt(20.0)
            .build()
        mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 1000)
        // setDestinationContainer.setVisibility(View.VISIBLE)
        // setPickUpContainer.setVisibility(View.GONE)
        pickUpLatLang = LatLng(centerPos.latitude, centerPos.longitude)
        if (style.getLayer("DROPPED_MARKER_LAYER_ID_PICKUP") != null) {
            val source = style.getSourceAs<GeoJsonSource>("dropped-marker-source-id-pickup")
            source?.setGeoJson(Point.fromLngLat(centerPos.longitude, centerPos.latitude))
            val pickUpMarker = style.getLayer("DROPPED_MARKER_LAYER_ID_PICKUP")
            pickUpMarker?.setProperties(PropertyFactory.visibility(Property.VISIBLE))
        }
        // textprogress.setVisibility(View.VISIBLE)

        pickup = Point.fromLngLat(centerPos.longitude, centerPos.latitude)
        // getaddress(pickup!!, mapboxMap, pickUpText)
    }
    private fun onDestination(style: Style, mapboxMap: MapboxMap, centerPos: LatLng) {
        if (style.getLayer("DROPPED_MARKER_LAYER_ID_DEST") != null) {
            val source = style.getSourceAs<GeoJsonSource>("dropped-marker-source-id-dest")
            source?.setGeoJson(Point.fromLngLat(centerPos.longitude, centerPos.latitude))
            val destMarker = style.getLayer("DROPPED_MARKER_LAYER_ID_DEST")
            destMarker?.setProperties(PropertyFactory.visibility(Property.VISIBLE))
        }
        destinationLatLang = LatLng(centerPos.latitude, centerPos.longitude)
        //  setDestinationContainer.setVisibility(View.GONE)
        if (pickUpText.text.toString().isEmpty()) {
            // setPickUpContainer.setVisibility(View.VISIBLE)
        } else {
            // setPickUpContainer.setVisibility(View.GONE)
        }
        destination = Point.fromLngLat(centerPos.longitude, centerPos.latitude)
        getaddress(destination!!, mapboxMap, destinationText)
    }
    private fun getaddress(point: Point, mapboxMap: MapboxMap, textView: TextView) {
        try {
            val client = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(point.longitude(), point.latitude()))
                .build()
            client.enqueueCall(object : Callback<GeocodingResponse?> {
                override fun onResponse(
                    call: Call<GeocodingResponse?>,
                    response: Response<GeocodingResponse?>
                ) {
                    if (response.body() != null) {
                        val results = response.body()!!.features()
                        if (results.size > 0) {
                            val feature = results[0]
                            mapboxMap.getStyle { style: Style? ->
                                textView.text = feature.placeName()
                                getRoute(mapboxMap)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GeocodingResponse?>, throwable: Throwable) {}
            })
        } catch (servicesException: ServicesException) {
            Timber.e("Error geocoding: %s", servicesException.toString())
            servicesException.printStackTrace()
        }
    }
    private fun initDroppedMarkerdestination(loadedMapStyle: Style) {
        loadedMapStyle.addImage(
            "dropped-icon-image-dest",
            BitmapUtils.getBitmapFromDrawable(resources.getDrawable(R.drawable.ic_baseline_location_on_24))!!
        )
        loadedMapStyle.addSource(GeoJsonSource("dropped-marker-source-id-dest"))
        loadedMapStyle.addLayer(
            SymbolLayer(
                "DROPPED_MARKER_LAYER_ID_DEST",
                "dropped-marker-source-id-dest"
            ).withProperties(
                PropertyFactory.iconImage("dropped-icon-image-dest"),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.visibility(Property.NONE),
                PropertyFactory.iconSize(2.0f),
                PropertyFactory.iconIgnorePlacement(true)
            )
        )
    }
    private fun initDroppedMarkerpickup(loadedMapStyle: Style) {
        loadedMapStyle.addImage(
            "dropped-icon-image-pickup",
            BitmapUtils.getBitmapFromDrawable(resources.getDrawable(R.drawable.ic_baseline_location_red))!!
        )
        loadedMapStyle.addSource(GeoJsonSource("dropped-marker-source-id-pickup"))
        loadedMapStyle.addLayer(
            SymbolLayer(
                "DROPPED_MARKER_LAYER_ID_PICKUP",
                "dropped-marker-source-id-pickup"
            ).withProperties(
                PropertyFactory.iconImage("dropped-icon-image-pickup"),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.visibility(Property.NONE),
                PropertyFactory.iconSize(2.0f),
                PropertyFactory.iconIgnorePlacement(true)
            )
        )
    }
    private fun drawNavigationPolylineRoute(route: DirectionsRoute, mapboxMap: MapboxMap?) {
        mapboxMap?.getStyle { style: Style ->
            val directionsRouteFeatureList: MutableList<Feature> =
                ArrayList()
            val lineString = LineString.fromPolyline(
                Objects.requireNonNull(route.geometry()!!), Constants.PRECISION_6
            )
            val coordinates =
                lineString.coordinates()
            for (i in coordinates.indices) {
                directionsRouteFeatureList.add(
                    Feature.fromGeometry(
                        LineString.fromLngLats(
                            coordinates
                        )
                    )
                )
            }
            val dashedLineDirectionsFeatureCollection =
                FeatureCollection.fromFeatures(directionsRouteFeatureList)
            val source = style.getSourceAs<GeoJsonSource>("SOURCE_ID")
            source?.setGeoJson(dashedLineDirectionsFeatureCollection)
        }
    }
    private fun getRoute(mapboxMap: MapboxMap) {
        if (pickup != null || destination != null) {
            // rlprogress.setVisibility(View.VISIBLE)
            val client = MapboxDirections.builder()
                .origin(pickup!!)
                .destination(destination!!)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING_TRAFFIC)
                .accessToken(getString(R.string.mapbox_access_token))
                .build()
            client.enqueueCall(object : Callback<DirectionsResponse?> {
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
                    val currentroute = response.body()!!.routes()[0]
                    drawNavigationPolylineRoute(currentroute, mapboxMap)

// Get the directions route
                    //  currentRoute = response.body()!!.routes().get(0)


//                    mapboxMap?.getStyle(object : Style.OnStyleLoaded {
//                        override fun onStyleLoaded(style: Style) {
//
//                            // Retrieve and update the source designated for showing the directions route
//                            val source: GeoJsonSource? = style.getSourceAs(ROUTE_SOURCE_ID)
//
//                            // Create a LineString with the directions route's geometry and
//                            // reset the GeoJSON source for the route LineLayer source
//                            if (source != null) {
//                                source.setGeoJson(
//                                    LineString.fromPolyline(
//                                        currentRoute!!.geometry()!!,
//                                        PRECISION_6
//                                    )
//                                )
//                            }
//                        }
//                    })
                }

                override fun onFailure(call: Call<DirectionsResponse?>?, throwable: Throwable) {
                    Timber.e("Error: " + throwable.message)
                    Toast.makeText(
                        requireContext(), "Error: " + throwable.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {

        }
    }
    private fun enableLocationComponent(loadedMapStyle: Style) {
// Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {

// Get an instance of the component
            val locationComponent: LocationComponent = mapboxmap.getLocationComponent()

// Set the LocationComponent activation options
            val locationComponentActivationOptions =
                LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                    .useDefaultLocationEngine(false)
                    .build()

// Activate with the LocationComponentActivationOptions object
            locationComponent.activateLocationComponent(locationComponentActivationOptions)

// Enable to make component visible
            locationComponent.isLocationComponentEnabled = true

// Set the component's camera mode
            locationComponent.cameraMode = CameraMode.TRACKING

// Set the component's render mode
            locationComponent.renderMode = RenderMode.COMPASS

            initLocationEngine()
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(requireActivity())
        }
    }
    private fun initLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(requireContext())
        val request = LocationEngineRequest.Builder(DEFAULT_INTERVAL_IN_MILLISECONDS)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(DEFAULT_MAX_WAIT_TIME).build()
        locationEngine!!.requestLocationUpdates(request, callback, Looper.getMainLooper())
        locationEngine!!.getLastLocation(callback)
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onExplanationNeeded(permissionsToExplain: List<String?>?) {
//        Toast.makeText(
//            this, R.string.user_location_permission_explanation,
//            Toast.LENGTH_LONG
//        ).show()
    }
    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            mapboxmap.getStyle(object : Style.OnStyleLoaded {
                override fun onStyleLoaded(style: Style) {
                    enableLocationComponent(style)
                }
            })
        } else {
//            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG)
//                .show()H
        }
    }
    private class LocationChangeListeningActivityLocationCallback internal constructor(activity: DashboardFragment?) :
        LocationEngineCallback<LocationEngineResult> {
        private val activityWeakReference: WeakReference<DashboardFragment>

        /**
         * The LocationEngineCallback interface's method which fires when the device's location has changed.
         *
         * @param result the LocationEngineResult object which has the last known location within it.
         */
        override fun onSuccess(result: LocationEngineResult) {
            val activity: DashboardFragment? = activityWeakReference.get()
            if (activity != null) {
                val location = result.lastLocation ?: return
                val position =
                    CameraPosition.Builder()
                        .target(LatLng( location.latitude,location.longitude))
                        .zoom(15.0)
                        .tilt(20.0)
                        .build()
                activity.mapboxmap.animateCamera(CameraUpdateFactory.newCameraPosition(position), 3000)
                activity.currentLocation.setOnClickListener { view: View? ->
                    activity. mapboxmap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(position),
                        3000
                    )

                }
// Create a Toast which displays the new location's coordinates

                // Toast.makeText(activity,"Bhanu  "+,Toast.LENGTH_LONG)


// Pass the new location to the Maps SDK's LocationComponent
                if (activity.mapboxmap != null && result.lastLocation != null) {
                    activity.mapboxmap.getLocationComponent()
                        .forceLocationUpdate(result.lastLocation)
                }
                else{

                }
            }
            else{

            }
        }

        /**
         * The LocationEngineCallback interface's method which fires when the device's location can't be captured
         *
         * @param exception the exception message
         */
        override fun onFailure(exception: java.lang.Exception) {
            val activity: DashboardFragment? = activityWeakReference.get()
            if (activity != null) {
//                Toast.makeText(
//                    activity, exception.localizedMessage,
//                    Toast.LENGTH_SHORT
//                ).show()
            }
        }

        init {
            activityWeakReference = WeakReference(activity)
        }
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