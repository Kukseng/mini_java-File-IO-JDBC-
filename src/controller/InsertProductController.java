package controller;

import model.service.InsertProductService;
import view.TimeView;

public class InsertProductController {
    private final InsertProductService productService;
    private final TimeView timeView;

    public InsertProductController(InsertProductService productService, TimeView timeView) {
        this.productService = productService;
        this.timeView = timeView;
    }

    public void runBenchmark() {
        try {
            // Run insert benchmark
            long insertTime = productService.insert10Million();
            timeView.showInsertTime(insertTime);

            // Run read benchmark
            long readTime = productService.read10Million();
            timeView.showReadTime(readTime);
        } catch (Exception e) {
            System.err.println("Benchmark Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
