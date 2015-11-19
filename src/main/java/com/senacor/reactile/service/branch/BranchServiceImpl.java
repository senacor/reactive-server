package com.senacor.reactile.service.branch;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.senacor.reactile.json.JsonizableList;
import com.senacor.reactile.service.customer.CustomerServiceImpl;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.impl.LoggerFactory;
import io.vertx.rxjava.ext.mongo.MongoService;
import rx.Observable;
import rx.functions.Func1;

public class BranchServiceImpl implements BranchService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	public static final String COLLECTION = "branches";
	private final MongoService mongoService;

	@Inject
	public BranchServiceImpl(MongoService mongoService) {
		this.mongoService = mongoService;
	}

	public Observable<Branch> getBranch(BranchId branchId) {
		return mongoService.findOneObservable(COLLECTION, branchId.toJson(), null).map(Branch::fromJson);
	}

	public Observable<BranchList> findBranches(JsonizableList<BranchId> branchIds) {
		
		JsonizableList<String> flatList = new JsonizableList<String>(convertList(branchIds.toList(), branchId -> branchId.getId()));

		Object values = flatList.toJson().getValue("items");
		
		logger.info("findBranches "+ values);
		
		JsonObject search = new JsonObject().put("$in", values);
		JsonObject query = new JsonObject().put("id", search);

		return executeQuery(query);

	}

	public Observable<BranchList> getAllBranches() {
		JsonObject query = new JsonObject();
		return executeQuery(query);
	}

	private Observable<BranchList> executeQuery(JsonObject query) {
		return mongoService.findObservable(COLLECTION, query).map(toTransactionList());
	}

	private Func1<List<JsonObject>, BranchList> toTransactionList() {
		return list -> {
			List<Branch> branches = list.stream().map(Branch::fromJson).collect(toList());
			return new BranchList(branches);
		};
	}
	
	//for lists
	public static <T, U> List<U> convertList(List<T> from, Function<T, U> func){
	    return from.stream().map(func).collect(Collectors.toList());
	}


}
