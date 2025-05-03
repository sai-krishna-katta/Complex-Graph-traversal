import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class GraphWorkFlow {
	private static Map<Integer,String> nodeNames=new HashMap<>();
	private static Map<Integer,List<Integer>> adjacencyList=new HashMap<>();
	private static Map<Integer,Integer> dependencyCount=new HashMap<>();
	private static Set<Integer> executedNodes=ConcurrentHashMap.newKeySet();
	private static ExecutorService threadPool=Executors.newCachedThreadPool();
	
public static void main(String[] args) {
	Scanner input=new Scanner(System.in);
	int totalNodes=Integer.parseInt(input.nextLine().trim());
	for(int i=0;i<totalNodes;i++) {
		String[] parts=input.nextLine().trim().split(":");
		int nodeId=Integer.parseInt(parts[0].trim());
		String nodeLabel=parts[1].trim();
		nodeNames.put(nodeId, nodeLabel);
		adjacencyList.put(nodeId,new ArrayList<>());
		dependencyCount.put(nodeId, 0);
		
		
	}
	int totalEdges=Integer.parseInt(input.nextLine().trim());
	for(int i=0;i<totalEdges;i++) {
		String[] edgeParts=input.nextLine().trim().split(":");
		int parent=Integer.parseInt(edgeParts[0].trim());
		int child=Integer.parseInt(edgeParts[1].trim());
		adjacencyList.get(parent).add(child);
		dependencyCount.put(child, dependencyCount.get(child)+1);
		
		
	}
	for(List<Integer> children:adjacencyList.values()) {
		Collections.sort(children);
	}
	CompletableFuture<Void> start=CompletableFuture.runAsync(()->processNode(1),threadPool);
	start.join();

	System.out.println(executedNodes.size());
	threadPool.shutdown();
	
}
private static void processNode(int nodeId) {
	
	
		if(!executedNodes.add(nodeId)) return;
	
	
	System.out.println(nodeNames.get(nodeId));
	List<CompletableFuture<Void>> childFutures=new ArrayList<>();
	for(int childId:adjacencyList.getOrDefault(nodeId,Collections.emptyList())) {
		boolean readyToExecute=false;
		synchronized(dependencyCount) {
			dependencyCount.put(childId, dependencyCount.get(childId)-1);
			if(dependencyCount.get(childId)==0) {
				readyToExecute=true;
			}
		}
		if(readyToExecute) {
			CompletableFuture<Void> future=CompletableFuture.runAsync(()->processNode(childId),threadPool);
			childFutures.add(future);
			future.join();
		}
	}
	
}
}
