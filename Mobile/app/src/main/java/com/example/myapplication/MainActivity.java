package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Data.Local.Code.CodeEntity;
import com.example.myapplication.Data.Local.Element.ElementEntity;
import com.example.myapplication.Data.Local.Element.ElementRegionEntity;
import com.example.myapplication.Data.Local.FlowData;
import com.example.myapplication.Data.Local.Region.RegionEntity;
import com.example.myapplication.Data.Local.Route.RouteEntity;
import com.example.myapplication.Detection.DetectorActivity;
import com.example.myapplication.ElementList.Adapters.ListSelectitemAdapter;
import com.example.myapplication.ElementList.SelectelementsListActivity;
import com.example.myapplication.Flows.FlowActivity;
import com.example.myapplication.Models.CodeForRoutingDTO;
import com.example.myapplication.Models.CurrentRouteDTO;
import com.example.myapplication.Models.ElementDTO;
import com.example.myapplication.Models.Enums.ActionEventMain;
import com.example.myapplication.Models.Enums.IconItemState;
import com.example.myapplication.Models.GraphAdjacenciesDTO;
import com.example.myapplication.QRCode.QRVisionScannerActivity;
import com.example.myapplication.Utils.ConstantValue;
import com.example.myapplication.Utils.DijkstraAlgo;
import com.example.myapplication.Utils.Node;
import com.example.myapplication.Utils.Pair;
import com.example.myapplication.Utils.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/// TODO
// Save state of element
// if location is not found

public class MainActivity extends FlowActivity {

    private Button qrCodeGettinData, objDetection, qrCodeRouting, newList, exit;
    private FlowData flowData;
    private ListView listView;
    private RelativeLayout finishLayout;
    private ListSelectitemAdapter listSelectitemAdapter;
    private ElementDTO elementDTO;
//    private static List<ElementEntity> availableItem;
    private TextView textViewInfo;
    private static boolean getAllData = false;
//    private static boolean firstLoad = false;
//    private Lock lock = new ReentrantLock();
    private boolean restartRouting = false;
    private Pair<List<IconItemState>,List<String>> searchingItem;
    private Pair<List<IconItemState>,List<String>> notFondItem;
    private Pair<List<IconItemState>,List<String>> foundItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        qrCodeGettinData = findViewById(R.id.QrScanenrGettingInfo);
        objDetection = findViewById(R.id.objDetection);
        listView = findViewById(R.id.listElements);
        textViewInfo = findViewById(R.id.info);
        finishLayout = findViewById(R.id.finishLayout);
        qrCodeRouting = findViewById(R.id.QRScanninRoutting);
        newList = findViewById(R.id.newList);
        exit = findViewById(R.id.exit);

        initializeData();
        startQrCodeGettingDataActivity();
        startQrCodeRoutingActivity();
//        startObjectDetection();
        exitWhenIsFinish();
        createNewListWhenIsFinish();

    }

    private void createNewListWhenIsFinish() {
        newList.setOnClickListener(v ->{
            Intent intent = new Intent(this, SelectelementsListActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void exitWhenIsFinish() {
        exit.setOnClickListener(v ->{
            finishAndRemoveTask();
        });

    }


    private int dpToPx(Context context, float dp) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        return Math.round(dp * dm.density);
    }

    private void initializeData() {
        flowData = ViewModelProviders.of(this).get(FlowData.class);
        ActionEventMain actionEventMain = (ActionEventMain)  getIntent().getExtras().get("Action");

        switch (actionEventMain)
        {
            case GET_INFO_LOCATION:{
                elementDTO = (ElementDTO) getIntent().getExtras().get("Elements");
                listView.setVisibility(View.INVISIBLE);
//                listView.set
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(dpToPx(getApplicationContext(),15),
                        dpToPx(getApplicationContext(),300),
                        dpToPx(getApplicationContext(),15),
                        dpToPx(getApplicationContext(),0));
                textViewInfo.setLayoutParams(params);
//                textViewInfo.setText("Apasati butonul de mai jos pentru a obtine informatiile necesare localizarii obiectelor");
                textViewInfo.setText("Press the button below to get information about where items should be found. " +
                                "Check your Internet connection, it should be on!");
//                finishLayout.setVisibility(View.VISIBLE);
                break;
            }
            case SHOW_INFO_LOCATION:{

//                firstLoad = true;
                elementDTO = (ElementDTO) getIntent().getExtras().get("Elements");
                String qrCode = (String) getIntent().getExtras().get("QRCode");
                Log.i("BARCODE RESULT", qrCode);

//                Toast.makeText(getApplicationContext(), "Am detectat: " + qrCode,
//                        Toast.LENGTH_LONG).show();
                getAllData = false;

                getInfoLocation(qrCode);

                Log.i("On Activity result", "Finish");

                while (getAllData == false) {

                }
                Log.i("Get Data", "After");
                List<String> elementsSelected = elementDTO.getSelectItems();
                System.out.println(elementsSelected);

                List<IconItemState> findIconPreviewState = availableItem();
                System.out.println(findIconPreviewState);


                Pair<List<IconItemState>, List<String>> listElementAndState = sortDataByState(elementsSelected, findIconPreviewState);

                listSelectitemAdapter = new ListSelectitemAdapter(getApplicationContext(),
                        R.layout.activity_element, listElementAndState.second, true, listElementAndState.first);
                listView.setAdapter(listSelectitemAdapter);
                listView.setVisibility(View.VISIBLE);

//                objDetection.setVisibility(View.VISIBLE);
                qrCodeGettinData.setVisibility(View.GONE);
                qrCodeRouting.setVisibility(View.VISIBLE);
//                textViewInfo.setText("Apasati butonul de mai jos pentru a incepe navigarea spre obiectele selectate");
                textViewInfo.setText("Press the button  below to start navigation!");
                break;
            }
            case END_ROUTE_ACTIVITY: {
//                elementDTO = (ElementDTO) getIntent().getExtras().get("Elements");

//                List<String> elementsSelected = elementDTO.getSelectItems();
//                System.out.println(elementsSelected);
//
//                List<IconItemState> findIconPreviewState = availableItem();
//                System.out.println(findIconPreviewState);
//
//
//                Pair<List<IconItemState>, List<String>> listElementAndState = sortDataByState(elementsSelected, findIconPreviewState);
//
//                listSelectitemAdapter = new ListSelectitemAdapter(getApplicationContext(),
//                        R.layout.activity_element, listElementAndState.second, true, listElementAndState.first);
//                listView.setAdapter(listSelectitemAdapter);

                Pair<List<IconItemState>,List<String>> search = (Pair<List<IconItemState>,List<String>>)
                        getIntent().getExtras().get("SearchingItem");
                if(search != null)
                {
                    searchingItem = search;
                }
                else
                {
                    searchingItem = null;
                }

                Pair<List<IconItemState>,List<String>> found = (Pair<List<IconItemState>,List<String>>)
                        getIntent().getExtras().get("FoundItem");
                if(found != null)
                {
                    foundItem = found;
                }
                else
                {
                    foundItem = null;
                }

                Pair<List<IconItemState>,List<String>> notFound = (Pair<List<IconItemState>,List<String>>)
                        getIntent().getExtras().get("NotFoundItem");
                if(search != null)
                {
                    notFondItem = notFound;
                }
                else
                {
                    notFondItem = null;
                }

                List<String> elements = new ArrayList<>();
                List<IconItemState> iconItemStates = new ArrayList<>();
                
                elements.addAll(searchingItem.second);
                elements.addAll(foundItem.second);
                elements.addAll(notFondItem.second);
                iconItemStates.addAll(searchingItem.first);
                iconItemStates.addAll(foundItem.first);
                iconItemStates.addAll(notFondItem.first);

                listSelectitemAdapter = new ListSelectitemAdapter(getApplicationContext(),
                        R.layout.activity_element, elements, true, iconItemStates);
                listView.setAdapter(listSelectitemAdapter);

                textViewInfo.setText("Do you want to create a new item list?");

                finishLayout.setVisibility(View.VISIBLE);
                qrCodeGettinData.setVisibility(View.GONE);
                qrCodeRouting.setVisibility(View.GONE);
                objDetection.setVisibility(View.GONE);

                break;
            }
          
        }
    }

    private void startQrCodeRoutingActivity() {
        // This function will be use for navigation between region
        qrCodeRouting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QRVisionScannerActivity.class);

                Map<Integer, List<Pair<Integer, Integer>>> graphAdjacencies = getMapStructure();
                System.out.println("getting graph done");
                List<CodeEntity> codeEntityList = getCode();
//
                CodeForRoutingDTO codeForRoutingDTO = new CodeForRoutingDTO(codeEntityList);
                GraphAdjacenciesDTO graphAdjacenciesDTO = new GraphAdjacenciesDTO(graphAdjacencies);

                intent.putExtra("ConstantValue", ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_ROUTING);
                intent.putExtra("Routing", graphAdjacenciesDTO);
                intent.putExtra("Code", codeForRoutingDTO);
                intent.putExtra("SearchingItem",searchingItem);
                intent.putExtra("FoundItem", foundItem);
                intent.putExtra("NotFoundItem", notFondItem);
//                intent.putExtra("Elements", elementDTO);
//                intent.putExtra("FlowData", flowData);
                
                startActivity(intent);
                finish();

            }
        });
    }

    @SuppressLint("LongLogTag")
    private Map<String, List<String>> sortDataByRegion(List<String> elementsSelected) {
        Map<String, List<String>> listOfElementInRegion = new HashMap<>();

        for (String element : elementsSelected) {
            getAllData = false;

            AtomicReference<ElementEntity> elementEntity = new AtomicReference<>();
            flowData.getElementByNameWithoutSingle(element)
                    .subscribeOn(Schedulers.single())
                    .observeOn(Schedulers.single())
                    .subscribe(new SingleObserver<Result<ElementEntity>>() {

                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@NonNull Result<ElementEntity> elementEntityResult) {
                            elementEntity.set(elementEntityResult.getResult());
                            Log.i("Element", "Success " + elementEntityResult.getResult().getName());
                            getAllData = true;
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            getAllData = true;
                            Log.i("Element", "Failed");
                        }
                    });

            while (getAllData == false) {

            }

            if (elementEntity.get() != null) {
                AtomicReference<ElementRegionEntity> elementRegionEntity = new AtomicReference<>();
                getAllData = false;
                flowData.getEntityRegion(elementEntity.get().getId())
                        .subscribeOn(Schedulers.single())
                        .observeOn(Schedulers.single())
                        .subscribe(new SingleObserver<Result<ElementRegionEntity>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@NonNull Result<ElementRegionEntity> elementRegionEntityResult) {
                                elementRegionEntity.set(elementRegionEntityResult.getResult());
                                Log.i("ElementRegion", "Success");
                                getAllData = true;

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                Log.i("ElementRegion", "Failed");
                                getAllData = true;
                            }
                        });
                while (getAllData == false) {

                }
                if (elementRegionEntity.get() != null) {
                    AtomicReference<RegionEntity> regionEntity = new AtomicReference<>();
                    getAllData = false;
                    flowData.getRegionById(elementRegionEntity.get().getRegion())
                            .subscribeOn(Schedulers.single())
                            .observeOn(Schedulers.single())
                            .subscribe(new SingleObserver<Result<RegionEntity>>() {
                                @Override
                                public void onSubscribe(@NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@NonNull Result<RegionEntity> regionEntityResult) {
                                    regionEntity.set(regionEntityResult.getResult());
                                    Log.i("Region", "Success " + regionEntityResult.getResult().getName());
                                    getAllData = true;
                                }

                                @Override
                                public void onError(@NonNull Throwable e) {
                                    Log.i("Region", "Failed");
                                    getAllData = true;
                                }
                            });
                    while (getAllData == false) {

                    }

                    if (regionEntity.get() != null) {
                        AtomicReference<CodeEntity> codeEntity = new AtomicReference<>();
                        getAllData = false;
                        flowData.getCodeEntityById(regionEntity.get().getCodeLocation())
                                .subscribeOn(Schedulers.single())
                                .observeOn(Schedulers.single())
                                .subscribe(new SingleObserver<Result<CodeEntity>>() {
                                    @Override
                                    public void onSubscribe(@NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(@NonNull Result<CodeEntity> codeEntityResult) {
                                        codeEntity.set(codeEntityResult.getResult());
                                        Log.i("Code", "Success " + codeEntityResult.getResult().getCode());
                                        getAllData = true;
                                    }

                                    @Override
                                    public void onError(@NonNull Throwable e) {
                                        Log.i("Code", "Failed");
                                        getAllData = true;
                                    }
                                });
                        while (getAllData == false) {

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
        return listOfElementInRegion;
    }

    private List<CodeEntity> getCode() {
        // Get all location code from db
        getAllData = false;
        AtomicReference<List<CodeEntity>> codeEntity = new AtomicReference<>();
//        synchronized(codeEntity)
//        {
//            lock.lock();
        flowData.getAllCodeDao()
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.single())
                .subscribe(new SingleObserver<Result<List<CodeEntity>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Result<List<CodeEntity>> listResult) {
                        codeEntity.set(listResult.getResult());
                        Log.i("Retrive Code", "Success");
                        getAllData = true;
//                        lock.unlock();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("Retrive Code", "Failed");
                        getAllData = true;
                    }
                });

        while (getAllData == false) {

        }
//        }
        Log.i("Code", String.valueOf(codeEntity.get().size()));
        return codeEntity.get();
    }

    private Map<Integer, List<Pair<Integer, Integer>>> getMapStructure() {
        // Getting info aboute routes and mapping data for navigation usage

        getAllData = false;
        AtomicReference<Map<Integer, List<Pair<Integer, Integer>>>> graphAdjacencies = new AtomicReference<>();

        flowData.getAllRouteDao()
//                .flatMap((result) -> {
//                    lock.lock();
//                    return (Single<Result<List<RouteEntity>>>) result.getResult();
//                })
                .subscribeOn(Schedulers.single())
                .observeOn(Schedulers.single())
                .subscribe(new SingleObserver<Result<List<RouteEntity>>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Result<List<RouteEntity>> listResult) {
                        Map<Integer, List<Pair<Integer, Integer>>> result = new HashMap<>();

                        if (listResult.getResult() != null) {
                            for (RouteEntity route : listResult.getResult()) {
                                if (result.containsKey(route.getCode1())) {
                                    List<Pair<Integer, Integer>> adjacencies = result.get(route.getCode1());
                                    adjacencies.add(new Pair<Integer, Integer>(route.getCode2(), route.getDistance()));
                                    result.put(route.getCode1(), adjacencies);
                                } else {
                                    List<Pair<Integer, Integer>> adjacencies = new ArrayList<>();
                                    adjacencies.add(new Pair<Integer, Integer>(route.getCode2(), route.getDistance()));
                                    result.put(route.getCode1(), adjacencies);
                                }

                            }

                            Log.i("Retrive route", "Success");
                            graphAdjacencies.set(result);
                        }

                        getAllData = true;
//                        lock.unlock();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                        Log.e("Retrive route", "Failed");
                        getAllData = true;
                    }
                });

        while (getAllData == false) {

        }

        Log.i("Graph", String.valueOf(graphAdjacencies.get().size()));
        return graphAdjacencies.get();
    }


//    private void startObjectDetection() {
//        // Function for starting activity for recognise object with camera
//        objDetection.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), DetectorActivity.class);
//                ElementDTO elementDetectionDTO = new ElementDTO();
//                List<String> elements = new ArrayList<>();
//                elements.add("cup");
//                elements.add("backpack");
//                elements.add("keyboard");
//                elements.add("bed");
//                elements.add("keyboard");
//                elements.add("cell phone");
//                elementDetectionDTO.setSelectItems(elements);
//                intent.putExtra("ItemsForDetection", elementDetectionDTO);
//                startActivity(intent);
//            }
//        });
//    }

    private void startQrCodeGettingDataActivity() {
        // Function for openning QR code activity and getting all needed data
        qrCodeGettinData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), QRVisionScannerActivity.class);
                intent.putExtra("Elements",  elementDTO);
                intent.putExtra("ConstantValue", ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_GETTING_LOCATION_INFO);
                startActivity(intent);
                finish();
//                startActivity(intent);
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        // Getting all data for a specific location and show them
//        if (requestCode == ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_GETTING_LOCATION_INFO) {
//            if (data != null) {
//                // Request code from QR Code integrator
//                // Get scanned bar code
////                try{
//                String qrCode = data.getStringExtra("QRCode");
//                Log.i("BARCODE RESULT", qrCode);
//
//                Toast.makeText(getApplicationContext(), "Am detectat: " + qrCode,
//                        Toast.LENGTH_LONG).show();
//                getAllData = false;
//
//                getInfoLocation(qrCode);
////                try {
////                    Thread.sleep(3000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
//                Log.i("On Activity result", "Finish");
//
//                while (getAllData == false) {
//
//                }
//                Log.i("Get Data", "After");
//                List<String> elementsSelected = elementDTO.getSelectItems();
//                System.out.println(elementsSelected);
//
//                List<IconItemState> findIconPreviewState = availableItem();
//                System.out.println(findIconPreviewState);
//
//
//                Pair<List<IconItemState>, List<String>> listElementAndState = sortDataByState(elementsSelected, findIconPreviewState);
//
//                listSelectitemAdapter = new ListSelectitemAdapter(getApplicationContext(),
//                        R.layout.activity_element, listElementAndState.second, true, listElementAndState.first);
//                listView.setAdapter(listSelectitemAdapter);
//                listView.setVisibility(View.VISIBLE);
//
//                qrCodeGettinData.setVisibility(View.GONE);
//                qrCodeRouting.setVisibility(View.VISIBLE);
//                textViewInfo.setText("Apasati butonul de mai jos pentru a incepe navigarea spre obiectele selectate");
////                }catch(Exception e)
////                {
////                    Log.i("ERROR result",e.getMessage().toString());
////                }
//
//
//            }
//        }
//        else{
//            if (requestCode == ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_ROUTING) {
//
////                while(new QRVisionScannerActivity().isFinishing())
//                restartRouting = true;
//
////                while(isFinishing())
////                {
////                    System.out.println("To be continue");
////                }
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        create
//    }

    private Pair<List<IconItemState>, List<String>> sortDataByState(List<String> elementsSelected, List<IconItemState> findIconPreviewState) {
        // Sort original selected item by IconItemState and show Alert if item are not found in the location

        List<String> stringSearching = new ArrayList<>();
        List<String> stringNotFound = new ArrayList<>();
        List<String> stringFound = new ArrayList<>();
        List<IconItemState> iconItemStateSearching = new ArrayList<>();
        List<IconItemState> iconItemStateNotFound = new ArrayList<>();
        List<IconItemState> iconItemStateFound = new ArrayList<>();

        boolean hasinvaliditem = false;
        Log.i("Get Data State", "After");
        int sizeArrayList = elementsSelected.size();
        for (int i = 0; i < sizeArrayList; i++) {
            if (findIconPreviewState.get(i) == IconItemState.NOT_FOUND) {
                stringNotFound.add(elementsSelected.get(i));
                iconItemStateNotFound.add(findIconPreviewState.get(i));
            }
            if(findIconPreviewState.get(i) == IconItemState.FOUND)
            {
                stringFound.add(elementsSelected.get(i));
                iconItemStateFound.add(findIconPreviewState.get(i));
            }
            if(findIconPreviewState.get(i) == IconItemState.SEARCHING)
            {
                stringSearching.add(elementsSelected.get(i));
                iconItemStateSearching.add(findIconPreviewState.get(i));
            }
        }

        elementsSelected.clear();
        findIconPreviewState.clear();

        searchingItem = new Pair<>(iconItemStateSearching,stringSearching);
        notFondItem = new Pair<>(iconItemStateNotFound,stringNotFound);
        foundItem = new Pair<>(iconItemStateFound,stringFound);

        elementsSelected.addAll(stringSearching);
        findIconPreviewState.addAll(iconItemStateSearching);

        elementsSelected.addAll(stringFound);
        findIconPreviewState.addAll(iconItemStateFound);

        elementsSelected.addAll(stringNotFound);
        findIconPreviewState.addAll(iconItemStateNotFound);

        if(stringNotFound.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Not all of the selected elements could be found in the current location")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, id) -> {
                        //do things
//                        firstLoad = false;
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        return new Pair<List<IconItemState>, List<String>>(findIconPreviewState, elementsSelected);

    }

    private List<IconItemState> availableItem() {
        // This check if all selected idem are available in the specific location and return the first status of all selected item

        List<IconItemState> findIconPreviewState;
        findIconPreviewState = new ArrayList<IconItemState>(Arrays.asList(new IconItemState[elementDTO.getSelectItems().size()]));
        Collections.fill(findIconPreviewState, IconItemState.SEARCHING);

        for (int i = 0; i < elementDTO.getSelectItems().size(); i++) {

            int finalI = i;
            getAllData = false;
            flowData.getElementByName(elementDTO.getSelectItems().get(finalI))

                    .subscribeOn(Schedulers.single())
                    .observeOn(Schedulers.single())
                    .subscribe(new SingleObserver<Result<ElementEntity>>() {

                        @Override
                        public void onSubscribe(Disposable d) {

                            disposable.add(d);

                        }

                        @Override
                        public void onSuccess(Result<ElementEntity> result) {


                            if (result.getResult() == null) {
//                                Toast.makeText(getApplicationContext(), "ERROR CHECK ITEM",
//                                        Toast.LENGTH_LONG).show();
                                Log.i("ERROR CHECK ITEM", "Element error table");
                                findIconPreviewState.set(finalI, IconItemState.NOT_FOUND);
//                            elementFoundInLocation.set(false);
                            } else {
//                                Toast.makeText(getApplicationContext(), "ITEM CHEKED",
//                                        Toast.LENGTH_LONG).show();
                                Log.i("ITEM CHEKED", result.getResult().getName());
//                                findIconPreviewState.set(finalI,IconItemState.FOUND);
//                                isInMemory.getAndSet(true);
//                                    availableItem.add(result.getResult());

                            }
                            getAllData = true;

                        }

                        @Override
                        public void onError(Throwable e) {
//                        elementFoundInLocation.set(false);
                            Log.e("ERROR onERR", "Element error table");
                            findIconPreviewState.set(finalI, IconItemState.NOT_FOUND);

                            getAllData = true;
//                            Toast.makeText(getApplicationContext(), "ERROR onERR",
//                                    Toast.LENGTH_LONG).show();
                        }
                    });
            while (getAllData == false) {

            }

        }


        return findIconPreviewState;
    }

    private void deleteExistingData()
    {
//        getAllData = false;
//
//        try {
//            Thread deleteData = new Thread(()->{
//                flowData.deleteAllCode();
//                flowData.deleteAllElement();
//                flowData.deleteAllElementRegion();
//                flowData.deleteAllRegion();
//                flowData.deleteAllRoute();
//                getAllData = true;
//            });
//
//            deleteData.start();
//            deleteData.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        while(getAllData == false)
//        {
//
//        }
        new Thread(() -> {
            flowData.deleteAllCode();
            flowData.deleteAllElement();
            flowData.deleteAllElementRegion();
            flowData.deleteAllRegion();
            flowData.deleteAllRoute();
        }).start();
    }

    private void getInfoLocation(String idLocationString) {

        // This method get all the require data for a location from the server if id is valid

        if (idLocationString != null && !idLocationString.equals("")) {
//            Integer idLocation= Integer.parseInt(idLocationString);
//            lock.lock();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            deleteExistingData();

            try {
                Single.zip(
                        flowData.getRoute(idLocationString),
                        flowData.getRegion(idLocationString),
                        flowData.getElements(idLocationString),
                        flowData.getElementRegion(idLocationString),
                        flowData.getCode(idLocationString),
                        (routeResponse, regionResponse, elementsResponse, elementRegionResponse, codeResponse) ->
                        {
                            try {

                                Single.zip(
                                        flowData.insertRoute(routeResponse.body()),
                                        flowData.insertRegion(regionResponse.body()),
                                        flowData.insertElement(elementsResponse.body()),
                                        flowData.insertElementRegion(elementRegionResponse.body()),
                                        flowData.insertCode(codeResponse.body()),
                                        (insertRoute, insertRegion, insertElement, insertElementRegion, insertCode) ->
                                        {
                                            Log.i("Insert", "After");
                                            getAllData = true;
                                            return true;
                                        }

                                )
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(Schedulers.single())
                                        .subscribe(new SingleObserver<Boolean>() {
                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(@NonNull Boolean aBoolean) {
                                                Log.i("Inserting Data", "Finish");
                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {
                                                Log.e("Inserting Data", "failed");
                                            }
                                        });

                                return true;
                            } catch (Exception e) {
                                Log.e("Getting data ERR", "FAILED");
                                return false;
                            }
                        }
                )
                        .subscribeOn(Schedulers.single())
                        .observeOn(Schedulers.single())
                        .subscribe(new SingleObserver<Boolean>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onSuccess(@NonNull Boolean aBoolean) {
                                Log.i("getting data", "Success");

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {

                            }
                        });

            } catch (Exception e) {
                Log.e("ERROR", e.getMessage().toString());
            }


//                    .subscribeOn(Schedulers.io())
//                    .observeOn(Schedulers.io())
//                    .subscribe(new SingleObserver<Boolean>() {
//                        @Override
//                        public void onSubscribe(Disposable d) {
////                          disposable.add(d);
//                        }
//
//                        @Override
//                        public void onSuccess(Boolean aBoolean) {
//
//                            Log.i( "Loading data","Data Loading is finish");
////                            synchronized (AndroidSchedulers.mainThread())
////                            {
////                                notify();
////                            }
//
//                            getAllData = true;
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            Toast.makeText(getApplicationContext(), "Something is wrong",
//                                    Toast.LENGTH_LONG).show();
//                        }
//                    });
        } else {
//            Toast.makeText(getApplicationContext(), "Nu s-a detectat niciun code, scanati iar",
//                    Toast.LENGTH_LONG).show();
        }


    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        flowData = ViewModelProviders.of(this).get(FlowData.class);
//
//        if(restartRouting)
//        {
//
//            List<String> elementsSelected = elementDTO.getSelectItems();
//            System.out.println(elementsSelected);
//
//            List<IconItemState> findIconPreviewState = availableItem();
//            System.out.println(findIconPreviewState);
//
//
//            Pair<List<IconItemState>, List<String>> listElementAndState = sortDataByState(elementsSelected, findIconPreviewState);
//
//            listSelectitemAdapter = new ListSelectitemAdapter(getApplicationContext(),
//                    R.layout.activity_element, listElementAndState.second, true, listElementAndState.first);
//            listView.setAdapter(listSelectitemAdapter);
//            listView.setVisibility(View.VISIBLE);
//
//            qrCodeGettinData.setVisibility(View.GONE);
//            qrCodeRouting.setVisibility(View.VISIBLE);
//            textViewInfo.setText("Apasati butonul de mai jos pentru a incepe navigarea spre obiectele selectate");
//            System.out.println("onActivityResult routing");
//            CurrentRouteDTO routeDTO = (CurrentRouteDTO) getIntent().getExtras().get("RemainingRoute");
//            CurrentRouteDTO importantPointsDTO = (CurrentRouteDTO) getIntent().getExtras().get("RemainingImportantPoint");
//            Map<Integer, List<Pair<Integer, Integer>>> graphAdjacencies = getMapStructure();
//            System.out.println("getting graph done");
//            List<String> newRoute = routeDTO.getCurrentRoute();
//
//            String location = newRoute.remove(0);
//            if (newRoute.size() != 0) {
//                routeDTO.setCurrentRoute(newRoute);
//
//                List<CodeEntity> codeEntityList = getCode();
////
//                CodeForRoutingDTO codeForRoutingDTO = new CodeForRoutingDTO(codeEntityList);
//                GraphAdjacenciesDTO graphAdjacenciesDTO = new GraphAdjacenciesDTO(graphAdjacencies);
//
//                Intent intent = new Intent(getApplicationContext(), QRVisionScannerActivity.class);
//                intent.putExtra("ConstantValue", ConstantValue.REQUEST_QR_CODE_SCANNING_FOR_ROUTING);
//                intent.putExtra("Routing", graphAdjacenciesDTO);
//                intent.putExtra("Code", codeForRoutingDTO);
//                intent.putExtra("Elements", elementDTO);
//                intent.putExtra("CurrentRoute", routeDTO);
//                intent.putExtra("CurrentImportantPoint", importantPointsDTO);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("Reopen activity from" + location)
//                        .setCancelable(false)
//                        .setPositiveButton("OK", (dialog, id) -> {
//                            //do things
//                            restartRouting = false;
//                            startActivity(intent);
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
//
////                restartRouting = false;
//            }
//
//
//        }
//    }

}