package luv.linux.crv;

/**
 * Created by linuxluv on 1/30/17.
 */

public class Worksite {
    private String name;
    private String address;
    private int workerAssigned;
    private String status;

    public Worksite(String name, String address, int workerAssigned, String status){
        this.name = name;
        this.address = address;
        this.workerAssigned = workerAssigned;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getWorkerAssigned() {
        return workerAssigned;
    }

    public void setWorkerAssigned(int workerAssigned) {
        this.workerAssigned = workerAssigned;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
