package com.example.myapplication.QRCode;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.myapplication.Data.Local.Code.CodeEntity;
import com.example.myapplication.Data.Local.Element.ElementEntity;
import com.example.myapplication.Data.Local.Element.ElementRegionEntity;
import com.example.myapplication.Data.Local.FlowData;
import com.example.myapplication.Data.Local.Region.RegionEntity;
import com.example.myapplication.Data.Local.Route.RouteEntity;
import com.example.myapplication.Detection.DetectorActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.Models.CodeForRoutingDTO;
import com.example.myapplication.Models.CurrentRouteDTO;
import com.example.myapplication.Models.ElementDTO;
import com.example.myapplication.Models.Enums.ActionEventMain;
import com.example.myapplication.Models.Enums.IconItemState;
import com.example.myapplication.Models.GraphAdjacenciesDTO;
import com.example.myapplication.QRCode.googlevision.ActivityCodeScannerBinding;
import com.example.myapplication.QRCode.googlevision.CameraXViewModel;
import com.example.myapplication.QRCode.googlevision.IExchangeScannedData;
import com.example.myapplication.QRCode.googlevision.IVisionImageProcessor;
import com.example.myapplication.QRCode.googlevision.QRCodeScannerProcessor;
import com.example.myapplication.Utils.ConstantValue;
import com.example.myapplication.Utils.DijkstraAlgo;
import com.example.myapplication.Utils.Edge;
import com.example.myapplication.Utils.Node;
import com.example.myapplication.Utils.Pair;
import com.example.myapplication.Utils.Result;
import com.google.mlkit.common.MlKitException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

// TODO
// de pus loading cat timp se calculeaza/recalculeaza ruta

public class QRVisionScannerActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback, IExchangeScannedData {

    private static final String TAG = "QRVisionScannerActivity";
    private static final int PERMISSION_REQUESTS = 1;
    private Integer constantValue;
    private ActivityCodeScannerBinding binding;

    private String lastScannedItemForRouting;
    private Pair<List<IconItemState>, List<String>> searchingItem;
    private Pair<List<IconItemState>, List<String>> notFondItem;
    private Pair<List<IconItemState>, List<String>> foundItem;

    @Nullable
    private ProcessCameraProvider cameraProvider;
    @Nullable
    private Preview previewUseCase;
    @Nullable
    private ImageAnalysis analysisUseCase;
    @Nullable
    private IVisionImageProcessor imageProcessor;
    private boolean needUpdateGraphicOverlayImageSourceInfo;

    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private CameraSelector cameraSelector;

    private static final String STATE_LENS_FACING = "lens_facing";
    private Map<Integer, List<Pair<Integer, Integer>>> graphAdjacencies;
    private List<CodeEntity> codeEntities;
    private ElementDTO elementDTO;
    private CurrentRouteDTO currentRouteDTO;
    private CurrentRouteDTO currentImportantRoutePoints;
    private Map<String, List<String>> regionElements;
    private static boolean getAllData = false;
    private FlowData flowDataFinal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startedActivity = true;
        Log.d(TAG, "onCreate");
        constantValue = (Integer) getIntent().getExtras().get("ConstantValue");


        GraphAdjacenciesDTO graphAdjacenciesDTO = (GraphAdjacenciesDTO) getIntent().getExtras().get("Routing");
        if (graphAdjacenciesDTO != null) {
            graphAdjacencies = graphAdjacenciesDTO.getGraph();

        } else {
            graphAdjacencies = null;
        }

        CodeForRoutingDTO codeForRoutingDTO = (CodeForRoutingDTO) getIntent().getExtras().get("Code");

        if (codeForRoutingDTO != null) {
            codeEntities = codeForRoutingDTO.getCodeEntityList();
        } else {
            codeEntities = null;
        }

        if (flowDataFinal == null) {
            flowDataFinal = ViewModelProviders.of(this).get(FlowData.class);
        }

        ElementDTO element = (ElementDTO) getIntent().getExtras().get("Elements");
        if (element != null) {
            elementDTO = element;
        } else {
            elementDTO = new ElementDTO();
        }
//        intent.putExtra("SearchingItem",searchingItem);
//        intent.putExtra("FoundItem", foundItem);
//        intent.putExtra("NotFoundItem", notFondItem);

        Pair<List<IconItemState>, List<String>> search = (Pair<List<IconItemState>, List<String>>)
                getIntent().getExtras().get("SearchingItem");
        if (search != null) {
            searchingItem = search;
        } else {
            searchingItem = null;
        }

        Pair<List<IconItemState>, List<String>> found = (Pair<List<IconItemState>, List<String>>)
                getIntent().getExtras().get("FoundItem");
        if (found != null) {
            foundItem = found;
        } else {
            foundItem = null;
        }

        Pair<List<IconItemState>, List<String>> notFound = (Pair<List<IconItemState>, List<String>>)
                getIntent().getExtras().get("NotFoundItem");
        if (search != null) {
            notFondItem = notFound;
        } else {
            notFondItem = null;
        }

        CurrentRouteDTO route = (CurrentRouteDTO) getIntent().getExtras().get("RemainingRoute");
        if (route != null) {
            currentRouteDTO = route;
        } else {
            currentRouteDTO = null;
        }

        CurrentRouteDTO importantPoints = (CurrentRouteDTO) getIntent().getExtras().get("RemainingImportantPoint");
        if (importantPoints != null) {
            currentImportantRoutePoints = importantPoints;
        } else {
            currentImportantRoutePoints = new CurrentRouteDTO();
        }

        if (savedInstanceState != null) {
            lensFacing = savedInstanceState.getInt(STATE_LENS_FACING, CameraSelector.LENS_FACING_BACK);
        }
        cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacing).build();
        binding = ActivityCodeScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(CameraXViewModel.class)
                .getProcessCameraProvider()
                .observe(
                        this,
                        provider -> {
                            cameraProvider = provider;
                            if (allPermissionsGranted()) {
                                bindAllCameraUseCases();
                            }
                        });

        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(STATE_LENS_FACING, lensFacing);
    }

    @Override
    public void onResume() {
        super.onResume();
        bindAllCameraUseCases();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageProcessor != null) {
            imageProcessor.stop();

        }

    }


    private void bindAllCameraUseCases() {
        bindPreviewUseCase();

        bindAnalysisUseCase();
    }

    private int flashStatus = 0;

    private void bindPreviewUseCase() {
        if (cameraProvider == null) {
            return;
        }
        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }


        previewUseCase = new Preview.Builder().build();
        previewUseCase.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        Camera cameraInstance = cameraProvider.bindToLifecycle(/* lifecycleOwner= */ QRVisionScannerActivity.this,
                cameraSelector, previewUseCase);
//        binding.flashBtn.setOnClickListener( event -> {
//            if (flashStatus == 0) {
//                cameraInstance.getCameraControl().enableTorch(true);
//                flashStatus = 1;
//            }
//            else {
//                cameraInstance.getCameraControl().enableTorch(false);
//                flashStatus = 0;
//            }
//        });

    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }
        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }
        if (imageProcessor != null) {
            imageProcessor.stop();
        }

        try {
            Log.i(TAG, "Using QRCode Detector Processor");
            imageProcessor = new QRCodeScannerProcessor(this, this);
        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor.", e);
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        analysisUseCase = builder.build();

        needUpdateGraphicOverlayImageSourceInfo = true;
        analysisUseCase.setAnalyzer(
                // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                // thus we can just runs the analyzer itself on main thread.
                ContextCompat.getMainExecutor(this),
                imageProxy -> {
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT;
                        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                        if (rotationDegrees == 0 || rotationDegrees == 180) {
                            binding.graphicOverlay.setImageSourceInfo(
                                    imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
                        } else {
                            binding.graphicOverlay.setImageSourceInfo(
                                    imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false;
                    }
                    try {
                        imageProcessor.processImageProxy(imageProxy, binding.graphicOverlay);
                    } catch (MlKitException e) {
                        Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, analysisUseCase);

    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            bindAllCameraUseCases();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }


    private List<String> getElementsOfARegion(String code) {
        List<String> elements = new ArrayList<>();
        AtomicReference<CodeEntity> codeEntity = new AtomicReference<>();
        getAllData = false;
        flowDataFinal.getCodeEntityByCode(code)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.single())
                .subscribe(new SingleObserver<Result<CodeEntity>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Result<CodeEntity> codeEntityResult) {
                        codeEntity.set(codeEntityResult.getResult());
                        Log.i("Code", "Success " + codeEntityResult.getResult().getCode());
                        getAllData = true;
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.i("Code", "Failed");
                        getAllData = true;
                    }
                });
        while (getAllData == false) {
            System.out.println("while code");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (codeEntity.get() != null) {
            AtomicReference<RegionEntity> regionEntity = new AtomicReference<>();
            getAllData = false;
            flowDataFinal.getRegionByCodeLocation(codeEntity.get().getId())
                    .subscribeOn(Schedulers.single())
                    .observeOn(Schedulers.single())
                    .subscribe(new SingleObserver<Result<RegionEntity>>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@io.reactivex.annotations.NonNull Result<RegionEntity> regionEntityResult) {
                            regionEntity.set(regionEntityResult.getResult());
                            Log.i("Region", "Success " + regionEntityResult.getResult().getName());
                            getAllData = true;
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            Log.i("Region", "Failed");
                            getAllData = true;
                        }
                    });
            while (getAllData == false) {
                System.out.println("while region");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (regionEntity.get() != null) {
                AtomicReference<List<ElementRegionEntity>> elementRegionEntity = new AtomicReference<>();
                getAllData = false;
                flowDataFinal.getEntityRegionByRegionId(regionEntity.get().getId())
                        .subscribeOn(Schedulers.single())
                        .observeOn(Schedulers.single())
                        .subscribe(new SingleObserver<Result<List<ElementRegionEntity>>>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull Result<List<ElementRegionEntity>> elementRegionEntityResult) {
                                elementRegionEntity.set(elementRegionEntityResult.getResult());
                                Log.i("ElementRegion", "Success");
                                getAllData = true;

                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                Log.i("ElementRegion", "Failed");
                                getAllData = true;
                            }
                        });
                while (getAllData == false) {
                    System.out.println("while elementregion");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (elementRegionEntity.get() != null) {
                    for (ElementRegionEntity elementRegion : elementRegionEntity.get()) {
                        getAllData = false;

                        AtomicReference<ElementEntity> elementEntity = new AtomicReference<>();
                        flowDataFinal.getElementById(elementRegion.getElement())
                                .subscribeOn(Schedulers.single())
                                .observeOn(Schedulers.single())
                                .subscribe(new SingleObserver<Result<ElementEntity>>() {

                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(@io.reactivex.annotations.NonNull Result<ElementEntity> elementEntityResult) {
                                        elementEntity.set(elementEntityResult.getResult());
                                        Log.i("Element", "Success " + elementEntityResult.getResult().getName());
                                        getAllData = true;
                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                        getAllData = true;
                                        Log.i("Element", "Failed");
                                    }
                                });

                        while (getAllData == false) {
                            System.out.println("while element");
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        if (elementEntity.get() != null) {
                            elements.add(elementEntity.get().getName());
                        }
                    }
                }
            }
        }
        return elements;
    }

    @SuppressLint("LongLogTag")
    private Map<String, List<String>> sortDataByRegion(List<String> elementsSelected) {
        Map<String, List<String>> listOfElementInRegion = new HashMap<>();

        for (String element : elementsSelected) {
            getAllData = false;

            AtomicReference<ElementEntity> elementEntity = new AtomicReference<>();
            flowDataFinal.getElementByNameWithoutSingle(element)
                    .subscribeOn(Schedulers.single())
                    .observeOn(Schedulers.single())
                    .subscribe(new SingleObserver<Result<ElementEntity>>() {

                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@io.reactivex.annotations.NonNull Result<ElementEntity> elementEntityResult) {
                            elementEntity.set(elementEntityResult.getResult());
                            Log.i("Element", "Success " + elementEntityResult.getResult().getName());
                            getAllData = true;
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            getAllData = true;
                            Log.i("Element", "Failed");
                        }
                    });

            while (getAllData == false) {
                System.out.println("while element");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (elementEntity.get() != null) {
                AtomicReference<ElementRegionEntity> elementRegionEntity = new AtomicReference<>();
                getAllData = false;
                flowDataFinal.getEntityRegion(elementEntity.get().getId())
                        .subscribeOn(Schedulers.single())
                        .observeOn(Schedulers.single())
                        .subscribe(new SingleObserver<Result<ElementRegionEntity>>() {
                            @Override
                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@io.reactivex.annotations.NonNull Result<ElementRegionEntity> elementRegionEntityResult) {
                                elementRegionEntity.set(elementRegionEntityResult.getResult());
                                Log.i("ElementRegion", "Success");
                                getAllData = true;

                            }

                            @Override
                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                Log.i("ElementRegion", "Failed");
                                getAllData = true;
                            }
                        });
                while (getAllData == false) {
                    System.out.println("while elementregion");
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (elementRegionEntity.get() != null) {
                    AtomicReference<RegionEntity> regionEntity = new AtomicReference<>();
                    getAllData = false;
                    flowDataFinal.getRegionById(elementRegionEntity.get().getRegion())
                            .subscribeOn(Schedulers.single())
                            .observeOn(Schedulers.single())
                            .subscribe(new SingleObserver<Result<RegionEntity>>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull Result<RegionEntity> regionEntityResult) {
                                    regionEntity.set(regionEntityResult.getResult());
                                    Log.i("Region", "Success " + regionEntityResult.getResult().getName());
                                    getAllData = true;
                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    Log.i("Region", "Failed");
                                    getAllData = true;
                                }
                            });
                    while (getAllData == false) {
                        System.out.println("while region");
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    if (regionEntity.get() != null) {
                        AtomicReference<CodeEntity> codeEntity = new AtomicReference<>();
                        getAllData = false;
                        flowDataFinal.getCodeEntityById(regionEntity.get().getCodeLocation())
                                .subscribeOn(Schedulers.single())
                                .observeOn(Schedulers.single())
                                .subscribe(new SingleObserver<Result<CodeEntity>>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(@io.reactivex.annotations.NonNull Result<CodeEntity> codeEntityResult) {
                                        codeEntity.set(codeEntityResult.getResult());
                                        Log.i("Code", "Success " + codeEntityResult.getResult().getCode());
                                        getAllData = true;
                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                        Log.i("Code", "Failed");
                                        getAllData = true;
                                    }
                                });
                        while (getAllData == false) {
                            System.out.println("while code");
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        if (codeEntity.get() != null) {
                            Log.i("Getting code for an element", "Success");
                            if (listOfElementInRegion.containsKey(codeEntity.get().getCode())) {
                                List<String> elements = listOfElementInRegion.get(codeEntity.get().getCode());
                                elements.add(element);
                                listOfElementInRegion.put(codeEntity.get().getCode(), elements);
                            } else {
                                List<String> elements = new ArrayList<>();
                                elements.add(element);
                                listOfElementInRegion.put(codeEntity.get().getCode(), elements);
                            }
                        }
                    }
                }
            }
        }

        regionElements = listOfElementInRegion;
        return listOfElementInRegion;
    }

    private List<Node> getPath(String pointToStart) {
        String startPoint = pointToStart;
        DijkstraAlgo.setMap(graphAdjacencies, codeEntities);
        System.out.println(DijkstraAlgo.nodes);


        Map<String, List<String>> listOfElementInRegion = sortDataByRegion(searchingItem.second);
//                DijkstraAlgo.setMap(graphAdjacenciesDTO.getGraph(),codeForRoutingDTO.getCodeEntityList());
        List<String> regionList = new ArrayList<>(listOfElementInRegion.keySet());
        List<Node> path = new ArrayList<>();
        if (regionList.contains(startPoint)) {
            Intent resultIntent = new Intent(getApplicationContext(), DetectorActivity.class);

            CodeForRoutingDTO codeForRoutingDTO = new CodeForRoutingDTO(codeEntities);
            GraphAdjacenciesDTO graphAdjacenciesDTO = new GraphAdjacenciesDTO(graphAdjacencies);

            List<String> elementOfFirstImportantRegion =
                    getElementsOfARegion(startPoint);
            List<String> elementSelectedFromFirstImportantRegion = new ArrayList<>();
            for (String elem : elementOfFirstImportantRegion) {
                if (searchingItem.second.contains(elem)) {
                    elementSelectedFromFirstImportantRegion.add(elem);
                }
            }

            ElementDTO elementInRegion = new ElementDTO();
            elementInRegion.setSelectItems(elementSelectedFromFirstImportantRegion);
//            CurrentRouteDTO newListImportantPoints = new CurrentRouteDTO(newCurrentImportantRoutePoints);
            CurrentRouteDTO newCurrentrouteDTO = new CurrentRouteDTO(Arrays.asList(startPoint));
            resultIntent.putExtra("RemainingRoute", newCurrentrouteDTO);
//            resultIntent.putExtra("RemainingImportantPoint", newListImportantPoints);
            resultIntent.putExtra("ConstantValue", ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_ROUTING);
            resultIntent.putExtra("Routing", graphAdjacenciesDTO);
            resultIntent.putExtra("Code", codeForRoutingDTO);
            resultIntent.putExtra("SearchingItem", searchingItem);
            resultIntent.putExtra("FoundItem", foundItem);
            resultIntent.putExtra("NotFoundItem", notFondItem);
            resultIntent.putExtra("Action", ActionEventMain.END_ROUTE_ACTIVITY);
            resultIntent.putExtra("ItemsForDetection", elementInRegion);
            startActivity(resultIntent);


            finish();


        } else {
            List<Node> nodeRegion = new ArrayList<>();

            for (String region : regionList) {
//                    System.out.println(DijkstraAlgo.nodes.get(DijkstraAlgo.nodes.indexOf(new Node(region))));
                nodeRegion.add(DijkstraAlgo.nodes.get(DijkstraAlgo.nodes.indexOf(new Node(region))));
                System.out.println(nodeRegion);
            }


//                if (listOfElementInRegion.containsKey(startPoint)) {
//                    int removePoze = nodeRegion.indexOf(new Node(startPoint));
////                    regionList.remove(removePoze);
//                    nodeRegion.remove(removePoze);
//
//
//                }
//
//        if (nodeRegion.contains(new Node(startPoint))) {
//            int removePoze = nodeRegion.indexOf(new Node(startPoint));
////                    regionList.remove(removePoze);
//            nodeRegion.remove(removePoze);
//
//
//        }

            String currentPoz = startPoint;
            List<String> importantPoints = new ArrayList<>();
            while (nodeRegion.size() > 0) {
//                    DijkstraAlgo.setMap(integerListMap, codeEntityList1);
//                    Collections.copy(DijkstraAlgo.nodes,DijkstraAlgo.unchangedNodes);
                for (Node node : DijkstraAlgo.nodes) {
                    node.parent = null;
                }

                System.out.println("Start node: " + currentPoz);
                System.out.println("Current element in nodes list: " + DijkstraAlgo.nodes.get(DijkstraAlgo.nodes.indexOf(new Node(currentPoz))));
                DijkstraAlgo.computePaths(DijkstraAlgo.nodes.get(DijkstraAlgo.nodes.indexOf(new Node(currentPoz))));
                List<Node> segmentNode = DijkstraAlgo.getShortestPathTo(nodeRegion);
                System.out.println("Segment: " + segmentNode);
                if (path.size() > 0) {
                    path.remove(path.size() - 1);
                }
                path.addAll(segmentNode);
                currentPoz = segmentNode.get(segmentNode.size() - 1).value;
                int removePoze = nodeRegion.indexOf(new Node(currentPoz));
//                    regionList.remove(removePoze);
                Node removedNode = nodeRegion.remove(removePoze);
                importantPoints.add(removedNode.value);
            }
            System.out.println("Path: " + path);
//        if(currentRouteDTO.getCurrentRoute() == null)
//        {
//            currentRouteDTO.setCurrentRoute(importantPoints);
//        }
            currentImportantRoutePoints.setCurrentRoute(importantPoints);
        }


        return path;
    }

    private Integer getIdOfCode(String code) {
        Integer id = null;
        for (CodeEntity codeEntity : codeEntities) {
            if (codeEntity.getCode().equals(code)) {
                id = codeEntity.getId();
                break;
            }
        }

        return id;

    }

    private String getNextDirection(String currentPoint, String nextPoint) {
        Integer idCurrentPoint = getIdOfCode(currentPoint);
        Integer idNextPoint = getIdOfCode(nextPoint);
        AtomicReference<String> direction = new AtomicReference<>();
        getAllData = false;
        flowDataFinal.getRouteUsingTwoPoints(idCurrentPoint, idNextPoint)
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.single())
                .subscribe(new SingleObserver<Result<RouteEntity>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Result<RouteEntity> routeEntityResult) {
                        if (routeEntityResult != null && routeEntityResult.getResult() != null) {
                            direction.set(routeEntityResult.getResult().getAid_message());
                            Log.i("Load aid message", "Success");
                        } else {
                            Log.e("Load aid message", "Failed");
                        }
                        getAllData = true;
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.e("Load aid message", "Failed");
                        getAllData = true;
                    }
                });
        while (getAllData == false) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return direction.get();
    }


    private boolean codeValidation(String code) {

        try{
            String[] validation = code.split("[,]");
            boolean firstPartCode, secondPartCode;
            Integer codeInt;
            try {
                codeInt = Integer.parseInt(validation[0]);
            } catch (NumberFormatException e) {
                codeInt = null;
            }

            if (codeInt == null)
                return false;

            if ((validation[1].length() != 3)
                    || ((validation[1].charAt(0) < 'A') || (validation[1].charAt(0) > 'Z'))
                    || ((validation[1].charAt(1) < 'A') || (validation[1].charAt(1) > 'Z'))
                    || ((validation[1].charAt(2) < 'A') || (validation[1].charAt(2) > 'Z'))) {
                return false;
            }
        }catch (Exception ex)
        {
            return false;
        }

        return true;
//
//        if(validation[1].size())

    }

    @Override
    public void sendScannedCode(String code) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {


            boolean validCode = codeValidation(code);

            if(validCode == true)
            {
                if (code != null && !code.isEmpty()) {

                    binding.resultContainer.setVisibility(View.VISIBLE);
                    // Is execute when is trying to get data for a location
                    if (constantValue == ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_GETTING_LOCATION_INFO) {
                        String[] parseCode = code.split("[,]");
//                        binding.qrcodeRawValue.setText(parseCode[0]);
                        Intent resultIntent = new Intent(this, MainActivity.class);
                        resultIntent.putExtra("QRCode", parseCode[0]);
                        resultIntent.putExtra("Elements", elementDTO);
                        resultIntent.putExtra("Action", ActionEventMain.SHOW_INFO_LOCATION);
                        startActivity(resultIntent);
                        finish();
//                    setResult(ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_GETTING_LOCATION_INFO, resultIntent);


                    } else {


                        // Is execute when is trying to navigate from a pozition to another in a specific location
                        if (constantValue == ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_ROUTING) {

                            if(binding.qrcodeRawValue.getText().toString() != null
                                    && !binding.qrcodeRawValue.getText().toString().equals(""))
                            {
                                binding.label.setText("Direction:");
                            }


                            boolean sameItme = false;
                            if (lastScannedItemForRouting == null) {
                                lastScannedItemForRouting = code;
                            } else {
                                if (code.equals(lastScannedItemForRouting)) {
                                    sameItme = true;
                                } else {
                                    lastScannedItemForRouting = code;
                                }
                            }

                            if (!sameItme) {
                                String[] parseCode = code.split("[,]");
//                            code = parseCode

// Check if a route is available
                                // A route is not available when the route was not calculated yet
                                // or if user is not in place where he was supposed to be
                                if (currentRouteDTO != null
                                        && currentRouteDTO.getCurrentRoute() != null
                                        && currentRouteDTO.getCurrentRoute().size() > 0
                                        && currentImportantRoutePoints != null
                                        && currentImportantRoutePoints.getCurrentRoute() != null
                                        && currentImportantRoutePoints.getCurrentRoute().size() > 0) {
//                            try {
                                    // Check if list of codes is not null and if adjacencies structure are loaded
//                                if (graphAdjacencies == null || codeEntities == null
//                                        || codeEntities.size() == 0
//                                        || currentRouteDTO.getCurrentRoute() == null
//                                        || currentRouteDTO.getCurrentRoute().size() == 0) {
//                                    try {
//                                        throw new Exception("Ceva nu a mers bine, incercati sa accesati din nou navigarea");
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }

                                    // Check if first element from route list is current position or not
                                    if (parseCode[1].equals(currentRouteDTO.getCurrentRoute().get(0))) {

                                        // Check if current pozition is an important one and if it is
                                        // finish activity and send current navigation progress
                                        if (currentRouteDTO.getCurrentRoute().get(0)
                                                .equals(currentImportantRoutePoints.getCurrentRoute().get(0))) {
                                            Intent resultIntent = new Intent(getApplicationContext(), DetectorActivity.class);
                                            List<String> newCurrentImportantRoutePoints = currentImportantRoutePoints.getCurrentRoute();
                                            newCurrentImportantRoutePoints.remove(0);
                                            currentImportantRoutePoints.setCurrentRoute(newCurrentImportantRoutePoints);
                                            CodeForRoutingDTO codeForRoutingDTO = new CodeForRoutingDTO(codeEntities);
                                            GraphAdjacenciesDTO graphAdjacenciesDTO = new GraphAdjacenciesDTO(graphAdjacencies);

                                            List<String> elementOfFirstImportantRegion =
                                                    getElementsOfARegion(currentRouteDTO.getCurrentRoute().get(0));
                                            List<String> elementSelectedFromFirstImportantRegion = new ArrayList<>();
                                            for (String elem : elementOfFirstImportantRegion) {
                                                if (searchingItem.second.contains(elem)) {
                                                    elementSelectedFromFirstImportantRegion.add(elem);
                                                }
                                            }

                                            ElementDTO elementInRegion = new ElementDTO();
                                            elementInRegion.setSelectItems(elementSelectedFromFirstImportantRegion);
                                            CurrentRouteDTO newListImportantPoints = new CurrentRouteDTO(newCurrentImportantRoutePoints);
                                            CurrentRouteDTO newCurrentrouteDTO = new CurrentRouteDTO(currentRouteDTO.getCurrentRoute());
                                            resultIntent.putExtra("RemainingRoute", newCurrentrouteDTO);
                                            resultIntent.putExtra("RemainingImportantPoint", newListImportantPoints);
                                            resultIntent.putExtra("ConstantValue", ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_ROUTING);
                                            resultIntent.putExtra("Routing", graphAdjacenciesDTO);
                                            resultIntent.putExtra("Code", codeForRoutingDTO);
                                            resultIntent.putExtra("SearchingItem", searchingItem);
                                            resultIntent.putExtra("FoundItem", foundItem);
                                            resultIntent.putExtra("NotFoundItem", notFondItem);
                                            resultIntent.putExtra("Action", ActionEventMain.END_ROUTE_ACTIVITY);
                                            resultIntent.putExtra("ItemsForDetection", elementInRegion);
                                            startActivity(resultIntent);


                                            finish();


//                                        onBackPressed();
                                        } else {

                                            // Getting aid message for navigation between current position and the next one
                                            String direction = getNextDirection(currentRouteDTO.getCurrentRoute().get(0),
                                                    currentRouteDTO.getCurrentRoute().get(1));
                                            currentRouteDTO.getCurrentRoute().remove(0);
                                            binding.qrcodeRawValue.setText(direction);
                                        }
                                    } else {
                                        if (codeEntities.get(codeEntities.indexOf(new CodeEntity(0, parseCode[1]))) != null) {
                                            Node currentNode = DijkstraAlgo.nodes.get(DijkstraAlgo.nodes.indexOf(new Node(parseCode[1])));
                                            boolean findPath = false;
                                            for (Edge edge : currentNode.adjacencies) {
                                                if (edge.target.value.equals(currentRouteDTO.getCurrentRoute().get(0))) {


                                                    // Getting aid message for navigation between current position and the next one
                                                    String direction = getNextDirection(parseCode[1], edge.target.value);
//                                                    currentRouteDTO.getCurrentRoute().remove(0);
                                                    binding.qrcodeRawValue.setText(direction);
                                                    findPath = true;
                                                    break;

                                                }

                                            }
                                            if (findPath == false) {
                                                List<Node> pathNode = getPath(parseCode[1]);
                                                List<String> pathNodeString = new ArrayList<>();
                                                for (Node node : pathNode) {
                                                    pathNodeString.add(node.value);
                                                }
                                                currentRouteDTO = new CurrentRouteDTO(pathNodeString);
                                                if (currentRouteDTO.getCurrentRoute().get(0)
                                                        .equals(currentImportantRoutePoints.getCurrentRoute().get(0))) {
                                                    Intent resultIntent = new Intent();
                                                    List<String> newCurrentImportantRoutePoints = currentImportantRoutePoints.getCurrentRoute();
                                                    newCurrentImportantRoutePoints.remove(0);
                                                    currentImportantRoutePoints.setCurrentRoute(newCurrentImportantRoutePoints);
                                                    CurrentRouteDTO newListImportantPoints = new CurrentRouteDTO(newCurrentImportantRoutePoints);
                                                    CurrentRouteDTO newCurrentrouteDTO = new CurrentRouteDTO(currentRouteDTO.getCurrentRoute());
                                                    resultIntent.putExtra("RemainingRoute", newCurrentrouteDTO);
                                                    resultIntent.putExtra("RemainingImportantPoint", newListImportantPoints);
                                                    setResult(ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_ROUTING, resultIntent);
                                                    finish();
                                                } else {

                                                    // Getting aid message for navigation between current position and the next one
                                                    String direction = getNextDirection(currentRouteDTO.getCurrentRoute().get(0),
                                                            currentRouteDTO.getCurrentRoute().get(1));
                                                    currentRouteDTO.getCurrentRoute().remove(0);
                                                    binding.qrcodeRawValue.setText(direction);
                                                }
                                            }
                                        } else {
                                            System.out.println("Scanati un code din locatia respectiva");
                                        }

                                    }

//                            } catch (Exception ex) {
//                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                                builder.setMessage(ex.getMessage())
//                                        .setCancelable(false)
//                                        .setPositiveButton("OK", (dialog, id) -> {
//                                            //do things
//                                            finish();
//                                        });
//                                AlertDialog alert = builder.create();
//                                alert.show();
//                            }

                                } else {
//                            currentRouteDTO = new CurrentRouteDTO();
                                    List<Node> pathNode = getPath(parseCode[1]);
                                    if (pathNode != null && pathNode.size() > 0) {
                                        List<String> pathNodeString = new ArrayList<>();
                                        for (Node node : pathNode) {
                                            pathNodeString.add(node.value);
                                        }

                                        currentRouteDTO = new CurrentRouteDTO(pathNodeString);
                                        if (currentRouteDTO.getCurrentRoute().get(0)
                                                .equals(currentImportantRoutePoints.getCurrentRoute().get(0))) {
                                            Intent resultIntent = new Intent();
                                            CurrentRouteDTO newCurrentrouteDTO = new CurrentRouteDTO(currentRouteDTO.getCurrentRoute());
                                            resultIntent.putExtra("RemainingRoute", newCurrentrouteDTO);
                                            setResult(ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_ROUTING, resultIntent);
                                            finish();
                                        } else {

                                            // Getting aid message for navigation between current position and the next one
                                            String direction = getNextDirection(currentRouteDTO.getCurrentRoute().get(0),
                                                    currentRouteDTO.getCurrentRoute().get(1));
                                            currentRouteDTO.getCurrentRoute().remove(0);
                                            binding.qrcodeRawValue.setText(direction);
                                        }
                                    }

                                }
                            }

                        }

                    }

                }
            }


        });
    }

}